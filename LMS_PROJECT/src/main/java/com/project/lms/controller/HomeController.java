package com.project.lms.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/")
	public String home() {
		return "index";
	}
	
	@GetMapping("logout")
	public String logout(HttpServletResponse response, 
						 HttpServletRequest request) {
	// 세션 로그아웃
	HttpSession session = request.getSession();
	if (session != null) {
		session.invalidate();
	}
	
	return "redirect:/";
	}
}
