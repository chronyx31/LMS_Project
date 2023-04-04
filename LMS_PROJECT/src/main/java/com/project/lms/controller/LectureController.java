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
@RequestMapping("subject")
public class LectureController {
	
	// 기본 경로
	@GetMapping("{subject_no}/lecture")
	public String goToLectrueBoard(@PathVariable Long subject_no) {
		
		return "subject/lecture/lecture";
	}

}