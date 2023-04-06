package com.project.lms.controller;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.lms.model.entity.subject.Subject;
import com.project.lms.repository.LectureMapper;
import com.project.lms.repository.SubjectMapper;
import com.project.lms.util.PageNavigator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("subject")
public class LectureController {
	
	private final SubjectMapper subjectMapper;
	private final LectureMapper lectureMapper;

	final int countPerPage = 3;//한 페이지에 표시될 게시글 숫자
	final int pagePerGroup = 5;//한번에 표시될 페이지의 수

	// 기본 경로
	@GetMapping("{subject_no}/lecture")
	public String goToLectrueBoard(@PathVariable Long subject_no, @RequestParam(defaultValue = "1") int page,
			  @RequestParam(required = false) String title_part, Model model) {
		// 검색 조건 없을 때, null 출력. subject_no 으로 총 갯수 검색
		// lecture는 메인 화면에 나올 일이 없으므로 category_name이 필요 없다.
		int total = lectureMapper.getTotal(title_part, subject_no);
		// 글이 없을 경우, total=0이되면 이상하니까 1로 처리.
		if (total < 1) {
			total = 1;
		}

		// 페이징 처리를 위한 navigator 객체 생성
		PageNavigator navi = new PageNavigator(countPerPage, pagePerGroup, page, total);
		model.addAttribute("navi", navi);
		RowBounds rb = new RowBounds(navi.getStartRecord(), navi.getCountPerPage());

		// subject를 읽기 위해서 불러오기
		Subject subject = subjectMapper.findSubjectByNo(subject_no);
		log.info("subject:{}", subject); 
		model.addAttribute("subject", subject);
		return "subject/lecture/lecture";
	}

}
