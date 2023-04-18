package com.project.lms.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.project.lms.model.entity.board.Lecture;
import com.project.lms.model.entity.board.Notification;
import com.project.lms.model.entity.member.Attendance;
import com.project.lms.model.entity.member.Member;
import com.project.lms.model.entity.member.MyLecture;
import com.project.lms.model.entity.subject.Subject;
import com.project.lms.repository.AttendanceMapper;
import com.project.lms.repository.LectureMapper;
import com.project.lms.repository.MylectureMapper;
import com.project.lms.repository.NotificationMapper;
import com.project.lms.repository.SubjectMapper;
import com.project.lms.util.PageNavigator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/subject")
public class NotificationController {

	private final SubjectMapper subjectMapper;
	private final NotificationMapper notificationMapper;
	private final MylectureMapper mylectureMapper;
	private final AttendanceMapper attendanceMapper;
	private final LectureMapper lectureMapper;
	
	final int countPerPage = 5;//한 페이지에 표시될 게시글 숫자
	final int pagePerGroup = 5;//한번에 표시될 페이지의 수


	
	
	
	// 과목 이동, 공지사항 게시판으로 이동
	@GetMapping("{subject_no}/notification")
	public String gotosubject(@PathVariable Long subject_no, @RequestParam(defaultValue = "1") int page,
			@RequestParam(required = false) String title_part,
			@RequestParam(required = false) String category_name,
			@SessionAttribute(name = "loginMember", required = false) Member loginMember,
			Model model) {

		// 공지사항글 페이징 처리용 객체 생성
		int total = notificationMapper.getTotal(title_part, category_name, subject_no);
		// 글이 없을 경우 total이 0으로 나오므로 페이지 카운트가 이상하게 나오기 때문에 total을 1로 만들어 처리를 하거나
		// 0일경우 페이지 선택이 안나오도록 html에서 처리할 필요가 있다고 봄.
		if (total < 1) {
			total = 1;
		}
		PageNavigator navi = new PageNavigator(countPerPage, pagePerGroup, page, total);
		RowBounds rb = new RowBounds(navi.getStartRecord(), navi.getCountPerPage());
		List<Notification> notifications = notificationMapper.getAllNotifications(rb, title_part, category_name, subject_no);
		Subject subject = subjectMapper.findSubjectByNo(subject_no);
		model.addAttribute("subject", subject);
		model.addAttribute("navi", navi);
		model.addAttribute("notifications", notifications);
		
		
		MyLecture mylecture = mylectureMapper.isMylectureExist(subject_no, loginMember.getMember_no());
			model.addAttribute("applyLecture", mylecture);
		
		
		
		return "subject/notification/notification";
	}

	// 글 읽기
	@GetMapping("{subject_no}/notification/read/{notification_no}")
	public String read(@PathVariable Long subject_no, @SessionAttribute(name = "loginMember", required = false) Member loginMember,
			@PathVariable Long notification_no, Model model) {

		// 과목명을 찾기 위한 객체 생성
		Subject subject = subjectMapper.findSubjectByNo(subject_no);
		Notification notification = notificationMapper.findNotificationByNo(notification_no);
		model.addAttribute("subject", subject);
		model.addAttribute("notification", notification);		

		MyLecture mylecture = mylectureMapper.isMylectureExist(subject_no, loginMember.getMember_no());
		model.addAttribute("applyLecture", mylecture);
		
		return "subject/notification/readNotification";
	}
	
	@GetMapping("{subject_no}/apply")
	public String applyLecture(@SessionAttribute(name = "loginMember", required = false) Member loginMember,
			@PathVariable Long subject_no,
			Model model) {
		//수강신청 버튼을 눌렀을 때, 버튼을 누른 사람의 회원번호와 그 강의번호를 저장하기 위한 MyLecture객체 생성
		MyLecture lecture = new MyLecture();
			//수강신청을 한 회원의 회원번호와 그 강의의 번호를 lecture에 저장
			lecture.setMember_no(loginMember.getMember_no());
			lecture.setSubject_no(subject_no);
			//회원번호와 과목번호가 저장된 객체를 DB에 저장
			mylectureMapper.ApplyLecture(lecture);
			
			//subject_no로 그 subject의 lecture의 개수 count(*)로 가져와서 각각의 lecture_no를 차례로 List에 
			List<Lecture> myLectures = lectureMapper.getAllMyLectures(subject_no);
			List<Attendance> myAttendances = new ArrayList<>();
			Attendance attendance = new Attendance();
			for (int i = 0; i < lectureMapper.getMyTotal(subject_no); i++) {
				attendance.setLecture_no(myLectures.get(i).getLecture_no());
				attendance.setMember_id(loginMember.getMember_id());
				attendance.setSubject_no(subject_no);
				attendanceMapper.createMyAttendance(attendance);
			}
			
			
		return "redirect:/subject/" + subject_no + "/notification";
	}

}
