package com.project.lms.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;

import com.project.lms.model.entity.member.Member;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CheckInterceptor implements HandlerInterceptor {

	// 세션에 계정 정보 있는지 확인, 없으면 로그인 페이지로 이동하게 만들기
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession();
		String requestURI = request.getRequestURI();
//		String paramId = request.getParameter("id") == null ? "" : "?id=" + request.getParameter("id");
//		Member member = null;
		// 현재 요청을 보낸 사람이 로그인이 되어있을 경우 => Controller 실행
		if (session.getAttribute("loginMember") != null) {
			log.info("인터셉터 로그인 인증 성공");
		} else {
			// 로그인이 되어있지 않을 경우 => Controller 실행 안되도록
			session.setAttribute("message", "로그인 후 이용바랍니다");
			response.sendRedirect("/members/login?redirectURL=" + requestURI);
			return false;
		}
		return true;
	}
}

