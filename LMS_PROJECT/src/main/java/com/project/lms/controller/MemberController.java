package com.project.lms.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.lms.model.dto.member.MemberJoinForm;
import com.project.lms.model.dto.member.MemberLoginForm;
import com.project.lms.model.entity.member.Member;
import com.project.lms.repository.MemberMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/members")
@Controller
public class MemberController {

	private final MemberMapper memberMapper;

	// 회원가입 폼 이동
	@GetMapping("join")
	public String joinForm(Model model) {
		model.addAttribute("members", new MemberJoinForm());
		return "members/join";
	}

	// 회원가입
	@PostMapping("join")
	public String join(@Validated @ModelAttribute("members") MemberJoinForm joinForm,
			BindingResult result) {
		log.info("joinForm: {}", joinForm);
		// 유효성 검사
		if (result.hasErrors()) {
			return "members/join";
		}
		Member findMember = memberMapper.findMemberById(joinForm.getMember_id());
		// 아이디 중복검사
		log.info("findMember: {}",findMember);
		if (findMember != null) {
			result.reject("joinFail", "이미 사용중인 아이디 입니다.");
			return "member/join";
		}
		// 회원가입
		memberMapper.joinMember(MemberJoinForm.toMember(joinForm));
		return "redirect:/";
	}

	// 로그인 폼 이동
	@GetMapping("login")
	public String loginForm(@RequestParam(defaultValue = "/") String redirectURL, Model model) {
		model.addAttribute("loginForm", new MemberLoginForm());
		model.addAttribute("redirectURL", redirectURL);
		return "members/login";
	}

	// 로그인
	@PostMapping("login")
	public String login(@Validated @ModelAttribute("loginForm") MemberLoginForm loginForm, BindingResult result,
			HttpServletResponse response, HttpServletRequest request,
			@RequestParam(defaultValue = "/") String redirectURL) {
		log.info("loginForm: {}", loginForm);
		// 유효성 검사
		if (result.hasErrors()) {
			return "members/login";
		}
		// 비밀번호 확인
		Member findMember = memberMapper.findMemberById(loginForm.getMember_id());
		if (findMember != null && findMember.getMember_password().equals(loginForm.getMember_password())) {
			log.info("로그인 성공");

			HttpSession session = request.getSession();
			session.setAttribute("loginMember", findMember);
			// 세션 유효기간 설정(초단위)
			session.setMaxInactiveInterval(3000);
		} else {
			log.info("로그인 실패");
			result.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다");
			return "members/login";
		}
		return "redirect:" + redirectURL;
	}
	
	@ResponseBody
	@GetMapping("checkId")
	public ResponseEntity<String> checkId(@RequestParam String member_id){
		// AJAX사용을 위한 아이디 중복검사
		log.info("member_id: {}",member_id);
		Member findMember = memberMapper.findMemberById(member_id);
		if(findMember == null) {
			return ResponseEntity.ok(null);
		}
		return ResponseEntity.ok(findMember.getMember_id());
	}

}
