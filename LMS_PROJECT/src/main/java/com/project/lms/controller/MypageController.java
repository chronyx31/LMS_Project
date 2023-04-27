package com.project.lms.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotBlank;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.project.lms.model.dto.member.MemberAttendance;
import com.project.lms.model.dto.member.MemberInfoForm;
import com.project.lms.model.dto.member.MemberModifyInfo;
import com.project.lms.model.dto.member.MemberModifyPass;
import com.project.lms.model.dto.member.MemberMyLecture;
import com.project.lms.model.entity.board.Assignment;
import com.project.lms.model.entity.member.Attendance;
import com.project.lms.model.entity.member.Member;
import com.project.lms.model.entity.member.MyLecture;
import com.project.lms.model.entity.subject.Subject;
import com.project.lms.repository.AttendanceMapper;
import com.project.lms.repository.MemberMapper;
import com.project.lms.repository.MylectureMapper;
import com.project.lms.repository.SubjectMapper;
import com.project.lms.util.PageNavigator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("members")
public class MypageController {

	private final MemberMapper memberMapper;
	private final MylectureMapper mylectureMapper;
	private final SubjectMapper subjectMapper;
	private final AttendanceMapper attendanceMapper;
	final int countPerPage = 10;//한 페이지에 표시될 게시글 숫자
	final int pagePerGroup = 5;//한번에 표시될 페이지의 수

	@GetMapping("mypage")
	public String goToMypage() {
		return "members/mypage";
	}
	
	@GetMapping("myinfo")
	public String goToMyinfo(HttpServletResponse response, HttpServletRequest request, Model model) {
		
		// 세션 가져오기
		HttpSession session = request.getSession();
		// 회원정보를 DTO에 담아 필요한 정보만 전해주기
		Member member = (Member) session.getAttribute("loginMember");
		MemberInfoForm myinfo = new MemberInfoForm(member);
		
		model.addAttribute("myinfo", myinfo);
		
		return "members/myinfo";
	}
	
	@GetMapping("modifyinfo")
	public String goToModifyInfo(HttpServletResponse response, HttpServletRequest request, Model model) {
		log.info("123");
		// 세션 가져오기
		HttpSession session = request.getSession();
		Member member = (Member) session.getAttribute("loginMember");
		MemberInfoForm modifyinfo = new MemberInfoForm(member);
		model.addAttribute("modifyinfo", modifyinfo);
		return "members/modifyinfo";
	}
	
	@PostMapping("modifyinfo")
	public String modifyInfo(@Validated @ModelAttribute("modifyinfo") MemberModifyInfo modify,
			BindingResult result, HttpServletResponse response, HttpServletRequest request) {
		if(result.hasErrors()) {
			return "members/modifyinfo";
		}Member member = modify.toMember(modify);
		log.info("membermodify: {}", modify);
		int complete = memberMapper.modifyMember(member);
		log.info("complete : {}", complete);
		return "redirect:/members/mypage";
	}
	
	@GetMapping("modifypass")
	public String modifyPassword(@SessionAttribute(value = "isCheckedPass", required = false) boolean password,
			HttpServletRequest request, Model model) {
		log.info("isCheckedPass: {}", password);
		if (password==false) {
			return "redirect:/members/checkpass";
		}
		MemberModifyPass modifyPass = new MemberModifyPass();
		model.addAttribute("modify", modifyPass);
		return "members/modifypass";
	}

	@PostMapping("modifypass")
	public String modifyPasswordPost(@Validated @ModelAttribute("modify")MemberModifyPass modify,
			BindingResult result, HttpServletResponse response, HttpServletRequest request) {
		if (result.hasErrors()) {
			return "members/modifypass";
		}
		Member member = modify.toMember(modify);
		log.info("membermodify: {}", modify);
		int complete = memberMapper.modifyMember(member);
		log.info("complete:{}", complete);
		return "redirect:/members/mypage";
	}

	@GetMapping("checkpass")
	public String checkPassword() {
		return "members/checkpass";
	}

	@PostMapping("checkpass")
	public String checkPasswordPost(@RequestParam String id, @RequestParam String password,
			HttpServletResponse response, HttpServletRequest request, Model model) {
		log.info("id:{}", id);
		log.info("password:{}", password);
		Member member = memberMapper.findMemberById(id);
		if (member != null && member.getMember_password().equals(password)) {
			log.info("member:{}", member);
			HttpSession session = request.getSession();
			session.setAttribute("isCheckedPass", true);
			return "redirect:/members/modifypass";
		}
		model.addAttribute("error", "일치하지 않습니다.");
		log.info("일치하지 않습니다.");
		return "members/checkpass";
	}
	
	@GetMapping("mylecture")
	public String goToMyLecture(MemberMyLecture myLec,
			@RequestParam(defaultValue = "1") int page,
			HttpServletRequest request,
			Model model) {
		
		final int countPerPage_mylec = 10;//한 페이지에 표시될 게시글 숫자
		final int pagePerGroup_mylec = 5;//한번에 표시될 페이지의 수
		// 세션 가져오기
		HttpSession session = request.getSession();
		Member member = (Member) session.getAttribute("loginMember");
		// 페이징 처리를 위한 객체 생성
		int total = mylectureMapper.getTotalofMyLecture(member.getMember_no());
		// 글이 없을 경우 total이 0으로 나오므로 페이지 카운트가 이상하게 나오기 때문에 total을 1로 만들어 처리를 하거나
		// 0일경우 페이지 선택이 안나오도록 html에서 처리할 필요가 있다고 봄.
		if (total < 1) {
			total = 1;
		}
		//페이징 처리를 위한 객체 생성
		PageNavigator navi = new PageNavigator(countPerPage_mylec, pagePerGroup_mylec, page, total);
		RowBounds rb = new RowBounds(navi.getStartRecord(), navi.getCountPerPage());
		
		//내 강의 목록 불러오기
		List<MemberMyLecture> mylectures = new ArrayList<>();
		mylectures = mylectureMapper.getAllMyLecture(rb, member.getMember_no());
		model.addAttribute("mylectures", mylectures);
		model.addAttribute("navi", navi);
		

		return"members/mylecture";
	}
	
	@GetMapping("{subject_no}/myattendance")
	public String gotomyattendance (@RequestParam(defaultValue = "1") int page,
			@SessionAttribute(name = "loginMember", required = false) Member loginMember,
			@PathVariable Long subject_no, Model model
			) {	
		//페이징 처리를 위해 내 출석 정보의 숫자 가져오기
		int total = mylectureMapper.getTotalMyAttendance(subject_no, loginMember.getMember_no());
		//total이 1보다 작을 경우에도 페이징 처리가 정상적으로 표시되도록 하기 위해 total을 1로 만들어 주기
		if (total < 1) {
			total = 1;
		}
		//페이징 처리를 위한 객체 생성
		PageNavigator navi = new PageNavigator(countPerPage, pagePerGroup, page, total);
		RowBounds rb = new RowBounds(navi.getStartRecord(), navi.getCountPerPage());
		//내 출석정보 가져오기
		List<MemberAttendance> myAttendances = mylectureMapper.getMyAttendance(rb, subject_no, loginMember.getMember_no()); 
		
		
		// subject를 읽기 위해서 불러오기
		Subject subject = subjectMapper.findSubjectByNo(subject_no);
		log.info("subject:{}", subject);
		model.addAttribute("subject", subject);
		model.addAttribute("attendances", myAttendances);
		model.addAttribute("navi", navi);
		return "members/myattendance";
	}
	
	@GetMapping("{subject_no}/myassignment")
	public String gotomyassignment (@RequestParam(defaultValue = "1") int page,
			@SessionAttribute(name = "loginMember", required = false) Member loginMember,
			@PathVariable Long subject_no, Model model) {
		// 페이징 처리를 위해 내가 제출한 과제목록의 숫자 가져오기
		int total = mylectureMapper.getTotalMyAssignment(subject_no, loginMember.getMember_id());
		//total이 1보다 작을 경우에도 페이징 처리가 정상적으로 표시되도록 하기 위해 total을 1로 만들어 주기
		if (total < 1) {
			total = 1;
		}
		//페이징 처리를 위한 객체 생성
		PageNavigator navi = new PageNavigator(countPerPage, pagePerGroup, page, total);
		RowBounds rb = new RowBounds(navi.getStartRecord(), navi.getCountPerPage());
		// 내가 제출한 과제목록 가져오기
		List<Assignment> myAssignments = mylectureMapper.getMyAssignment(rb, subject_no, loginMember.getMember_id());
		// subject를 읽기 위해서 불러오기
		Subject subject = subjectMapper.findSubjectByNo(subject_no);
		log.info("subject:{}", subject);
		model.addAttribute("subject", subject);
		model.addAttribute("assignments", myAssignments);
		model.addAttribute("navi", navi);
		
		return"members/myassignment";
	}
}
