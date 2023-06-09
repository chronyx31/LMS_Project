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
	final int countPerPage = 8;//한 페이지에 표시될 게시글 숫자
	final int pagePerGroup = 5;//한번에 표시될 페이지의 수
	
	// 과목 보기
	@GetMapping("subject")
	public String subjectList(@RequestParam(defaultValue = "1") int page,
			@RequestParam(required = false) String title_part,
			@RequestParam(required = false) String category_name,
			Model model) {
		// 페이징 처리를 위한 객체 생성
		int total = subjectMapper.getTotal(title_part, category_name);
		// 글이 없을 경우 total이 0으로 나오므로 페이지 카운트가 이상하게 나오기 때문에 total을 1로 만들어 처리를 하거나
		// 0일경우 페이지 선택이 안나오도록 html에서 처리할 필요가 있다고 봄.
		if (total < 1) {
			total = 1;
		}
		PageNavigator navi = new PageNavigator(countPerPage, pagePerGroup, page, total);
		RowBounds rb = new RowBounds(navi.getStartRecord(), navi.getCountPerPage());
		List<Subject> subjects = subjectMapper.getAllSubjects(rb, title_part, category_name);		
		log.info("navi: {}", navi);
		log.info("subjects: {}",subjects);
		model.addAttribute("navi", navi);
		model.addAttribute("subjects", subjects);
		model.addAttribute("category_name", category_name);
		model.addAttribute("title_part", title_part);
		
		return "subject/subject";
	}
	

}
