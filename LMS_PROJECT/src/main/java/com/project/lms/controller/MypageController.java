package com.project.lms.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotBlank;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.project.lms.model.dto.member.MemberInfoForm;
import com.project.lms.model.dto.member.MemberModifyPass;
import com.project.lms.model.entity.member.Member;
import com.project.lms.repository.MemberMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("members")
public class MypageController {

	private final MemberMapper memberMapper;

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
		
		// 세션 가져오기
		HttpSession session = request.getSession();
		Member member = (Member) session.getAttribute("loginMember");
		MemberInfoForm modifyinfo = new MemberInfoForm(member);
		model.addAttribute("modifyinfo", modifyinfo);
		
		return "members/modifyinfo";
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
}
