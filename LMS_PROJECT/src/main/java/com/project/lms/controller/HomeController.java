package com.project.lms.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.project.lms.model.entity.board.Notification;
import com.project.lms.model.entity.board.QNA;
import com.project.lms.repository.NotificationMapper;
import com.project.lms.repository.QnaMapper;
import com.project.lms.util.PageNavigator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
public class HomeController {
	private final NotificationMapper notificationMapper;
	private final QnaMapper qnaMapper;
	
	// index페이지에 최신 공지글을 보여주기 위한 방법으로 pagenavigator를 이용
	final int countPerPage = 2;		// index 페이지에 표시될 게시글 숫자
	final int pagePerGroup = 5;		// 한번에 표시될 페이지의 수 : 1페이지만 쓰기때문에 이용안함
	
	@GetMapping("/")
	public String home(Model model) {
		Notification noti = new Notification();
		
		// 공지사항 글을 불러오기 위한 객체 생성
		// 최신글 n개를 불러오는 방법으로 PageNavigator를 이용
		int total = notificationMapper.getTotal(null, "MAIN", null);
		PageNavigator navi = new PageNavigator(countPerPage, pagePerGroup, 1, total);
		RowBounds rb = new RowBounds(navi.getStartRecord(), navi.getCountPerPage());
		List<Notification> notifications = notificationMapper.getAllNotifications(rb, null, "MAIN", null);
		log.info("notifications:{}", notifications);
		model.addAttribute("notifications", notifications);
		
		QNA qna = new QNA();
		int total2 = qnaMapper.getTotal(null, "MAIN", null);
		PageNavigator navi2 = new PageNavigator(countPerPage, pagePerGroup, 1, total2);
		RowBounds rb2 = new RowBounds(navi2.getStartRecord(), navi2.getCountPerPage());
		List<QNA> qnas = qnaMapper.getAllQnas(rb2, null, "MAIN", null);
		log.info("qnas : {}", qnas);
		model.addAttribute("qnas", qnas);
		return "index";
	}
	
//	@GetMapping("/")
//	public String home2(Model model) {
//		QNA qna = new QNA();
//		int total2 = qnaMapper.getTotal(null, "MAIN", null);
//		PageNavigator navi2 = new PageNavigator(countPerPage, pagePerGroup, 1, total2);
//		RowBounds rb2 = new RowBounds(navi2.getStartRecord(), navi2.getCountPerPage());
//		List<QNA> qnas = qnaMapper.getAllQnas(rb2, null, "MAIN", null);
//		log.info("qnas : {}", qnas);
//		model.addAttribute("qnas", qnas);
//		return "index";
//	}
	
	// 로그아웃
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
