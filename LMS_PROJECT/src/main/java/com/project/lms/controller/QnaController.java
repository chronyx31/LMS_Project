package com.project.lms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/subject")
public class QnaController {

	// 기본경로
	@GetMapping("{subject_no}/qna")
	public String goToQna(@PathVariable Long subject_no) {
		return "subject/qna/qna";
	}
}
