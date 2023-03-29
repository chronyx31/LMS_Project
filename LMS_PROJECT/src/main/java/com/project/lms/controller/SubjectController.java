package com.project.lms.controller;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.lms.model.entity.board.Notification;
import com.project.lms.model.entity.subject.Subject;
import com.project.lms.repository.NotificationMapper;
import com.project.lms.repository.SubjectMapper;
import com.project.lms.util.PageNavigator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SubjectController {
	private final SubjectMapper subjectMapper;
	private final NotificationMapper notificationMapper;
	
	final int countPerPage = 5;//한 페이지에 표시될 게시글 숫자
	final int pagePerGroup = 5;//한번에 표시될 페이지의 수
	
	// 과목 보기
	@GetMapping("subject")
	public String subjectList(@RequestParam(defaultValue = "1") int page,
			@RequestParam(required = false) String title_part,
			@RequestParam(required = false) String category_name,
			Model model) {
		// 페이징 처리를 위한 객체 생성
		int total = subjectMapper.getTotal(title_part, category_name);
		PageNavigator navi = new PageNavigator(countPerPage, pagePerGroup, page, total);
		RowBounds rb = new RowBounds(navi.getStartRecord(), navi.getCountPerPage());
		List<Subject> subjects = subjectMapper.getAllSubjects(rb, title_part, category_name);
		log.info("subjects: {}",subjects);
		
		model.addAttribute("subjects", subjects);
		model.addAttribute("category_name", category_name);
		model.addAttribute("title_part", title_part);
		
		return "subject/subject";
	}
	
	// 과목 이동
	@GetMapping("subject/{subject_no}/notification")
	public String gotosubject(@PathVariable Long subject_no, @RequestParam(defaultValue = "1") int page,
			@RequestParam(required = false) String title_part,
			@RequestParam(required = false) String category_name,
			Model model) {
//		int total = notificationMapper.getTotal(title_part, category_name, subject_no);
//		PageNavigator navi = new PageNavigator(countPerPage, pagePerGroup, page, total);
//		RowBounds rb = new RowBounds(navi.getStartRecord(), navi.getCountPerPage());
//		List<Notification> subjects = notificationMapper.getAllNotifications(rb, title_part, category_name, subject_no);
//		log.info("subjects: {}",subjects);
//		
//		model.addAttribute("category_name", category_name);
//		model.addAttribute("title_part", title_part);
		Subject subject = subjectMapper.findSubjectByNo(subject_no);		
		log.info("subject : {}", subject);
		model.addAttribute("subject", subject);
		model.addAttribute("category_name", category_name);
		model.addAttribute("title_part", title_part);
		
		return "subject/notification";
	}
}
