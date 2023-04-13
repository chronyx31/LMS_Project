package com.project.lms.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.RowBounds;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.project.lms.model.entity.board.Lecture;
import com.project.lms.model.entity.member.Attendance;
import com.project.lms.model.entity.member.Member;
import com.project.lms.model.entity.member.MyLecture;
import com.project.lms.model.entity.subject.Subject;
import com.project.lms.repository.AttendanceMapper;
import com.project.lms.repository.LectureMapper;
import com.project.lms.repository.MylectureMapper;
import com.project.lms.repository.SubjectMapper;
import com.project.lms.util.PageNavigator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.mp4parser.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("subject")
public class LectureController {

	private final SubjectMapper subjectMapper;
	private final LectureMapper lectureMapper;
	private final MylectureMapper mylectureMapper;
	private final AttendanceMapper attendanceMapper;

	final int countPerPage = 3; // 한 페이지에 표시될 게시글 숫자
	final int pagePerGroup = 5; // 한번에 표시될 페이지의 수

	// 경로 탐색용 String이지만 절대경로로 동영상 재생시 재생이 거부됨. 상대경로를 사용할 필요가 있음.
	private final String rootPath = System.getProperty("user.dir");
	private final String lecturePath = rootPath + "\\src\\main\\resources\\static\\lectures\\";

	// 기본 경로
	@GetMapping("{subject_no}/lecture")
	public String goToLectrueBoard(@PathVariable Long subject_no, @RequestParam(defaultValue = "1") int page,
			@RequestParam(required = false) String title_part, Model model) {

		// 검색 조건 없을 때, null 출력. subject_no 으로 총 갯수 검색
		// lecture는 메인 화면에 나올 일이 없으므로 category_name이 필요 없다.
		int total = lectureMapper.getTotal(title_part, subject_no);
		// 글이 없을 경우, total=0이되면 이상하니까 1로 처리.
		if (total < 1) {
			total = 1;
		}

		// subject를 읽기 위해서 불러오기
		Subject subject = subjectMapper.findSubjectByNo(subject_no);
		log.info("subject:{}", subject);
		model.addAttribute("subject", subject);

		// 페이징 처리를 위한 navigator 객체 생성
		PageNavigator navi = new PageNavigator(countPerPage, pagePerGroup, page, total);
		model.addAttribute("navi", navi);
		RowBounds rb = new RowBounds(navi.getStartRecord(), navi.getCountPerPage());

		// 페이징 처리한 전체 글
		List<Lecture> lectures = lectureMapper.getAllLectures(rb, title_part, subject_no);
		model.addAttribute("lectures", lectures);

		return "subject/lecture/lecture";
	}

	@GetMapping("{subject_no}/lecture/read/{lecture_no}")
	public String readLecture(@PathVariable Long subject_no, Model model, @PathVariable Long lecture_no,
			@SessionAttribute(name = "loginMember", required = false) Member loginMember, HttpServletResponse response,
			HttpServletRequest request)
			throws IOException {

		// 수강신청을 했는지 확인하는 절차. 수강신청을 하지 않으면 강의를 볼 수 없다.
		// 어디서 수강신청확인을 하고 어디까지 뒤로 보낼것인지 조절해야 할 사안.
//		MyLecture isMylectureExist = mylectureMapper.isMylectureExist(subject_no, loginMember.getMember_no());
//		log.info("isExist:{}", isMylectureExist);
//		if (isMylectureExist == null) {
//			response.setContentType("text/html; charset=UTF-8");
//			PrintWriter out = response.getWriter();
//			out.println("<script>alert('수강신청을 먼저 해주시기 바랍니다.'); 
//			location.href='/subject/" + subject_no
//					+ "/lecture';</script>");
//			out.flush();
//			// return값에 redirect가 있으면 cannot render error가 일어남.
////			return "redirect:/subject/" + subject_no + "/lecture";
//			// return값이 없으면 아래의 코드도 전부 실행되기 때문에 return null로 끊어줄 필요가 있음. 
//			return null;
//		}

		// 글을 읽기 위한 객체 생성
		Subject subject = subjectMapper.findSubjectByNo(subject_no);
		Lecture lecture = lectureMapper.findLectureByNo(lecture_no);
		model.addAttribute("subject", subject);
		model.addAttribute("lecture", lecture);
		String fullPath = lecturePath + lecture.getLecture_contents() + ".mp4";
		File file = new File(fullPath);
		log.info("length : {}", file.length());
		IsoFile isoFile = new IsoFile(fullPath);

		// 전체 길이를 초로 변환
		double lengthInSeconds = (double) isoFile.getMovieBox().getMovieHeaderBox().getDuration()
				/ isoFile.getMovieBox().getMovieHeaderBox().getTimescale();
		isoFile.close();
		log.info("duration : {}", lengthInSeconds);

		// 출결체크를 위한 테이블 확인 없으면 만들고 있으면 불러온다.
		Attendance checkAttendance = attendanceMapper.findAttendance(lecture_no, loginMember.getMember_id());
		log.info("attend : {}", checkAttendance);
		if (checkAttendance == null) {
			Attendance createAttendance = new Attendance(subject_no, lecture_no, loginMember.getMember_id(),
					lengthInSeconds);
			log.info("attend : {}", createAttendance);
//			attendanceMapper.createAttendance(createAttendance);
			model.addAttribute("attendance", createAttendance);
		} else {
			model.addAttribute("attendance", checkAttendance);
		}

		Long time = System.currentTimeMillis();
		HttpSession session = request.getSession();
		session.setAttribute("lectureTime", time);
		log.info("time: {}", time);
		
		return "subject/lecture/readLecture";
	}

	@ResponseBody
	@PostMapping("{subject_no}/lecture/updateattendance")
	public ResponseEntity<String> update(@ModelAttribute Attendance attendance, HttpServletRequest request) {
		Long time = System.currentTimeMillis();
		log.info("ajax도착");
		HttpSession session = request.getSession();
		Long lectureTime = (Long) session.getAttribute("lectureTime");
		Long checkTime = time-lectureTime;
		log.info("time:{}", checkTime);
		log.info("attendance:{}", attendance);
		if(attendance.getVideo_point()>(attendance.getVideo_length()*0.9)) {
			log.info("출석체크 조건 만족");
			return ResponseEntity.ok("success");
		} else {
			log.info("출석체크 조건 불만족");
			return ResponseEntity.ok("notallowed");
		}
	}
}
