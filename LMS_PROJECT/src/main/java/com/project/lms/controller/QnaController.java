package com.project.lms.controller;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.project.lms.model.dto.board.QnaWriteForm;
import com.project.lms.model.entity.board.QNA;
import com.project.lms.model.entity.board.Reply;
import com.project.lms.model.entity.member.Member;
import com.project.lms.model.entity.subject.Subject;
import com.project.lms.repository.QnaMapper;
import com.project.lms.repository.ReplyMapper;
import com.project.lms.repository.SubjectMapper;
import com.project.lms.util.PageNavigator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/subject")
public class QnaController {

	private final SubjectMapper subjectMapper;
	private final QnaMapper qnaMapper;
	private final ReplyMapper replyMapper;

	final int countPerPage = 3;//한 페이지에 표시될 게시글 숫자
	final int pagePerGroup = 5;//한번에 표시될 페이지의 수

	// 기본경로
	@GetMapping("{subject_no}/qna")
	public String goToQna(@PathVariable Long subject_no, @RequestParam(defaultValue = "1") int page,
			  @RequestParam(required = false) String title_part, Model model) {
		// 검색 조건 없을 때, null 출력. subject_no 으로 총 갯수 검색
		int total = qnaMapper.getTotal(title_part, null, subject_no);
		// 글이 없을 경우, total=0이되면 이상하니까 1로 처리.
		if (total < 1) {
			total = 1;
		}

		// 페이징 처리를 위한 navigator 객체 생성
		PageNavigator navi = new PageNavigator(countPerPage, pagePerGroup, page, total);
		model.addAttribute("navi", navi);
		RowBounds rb = new RowBounds(navi.getStartRecord(), navi.getCountPerPage());

		// subject를 읽기 위해서 불러옴
		Subject subject = subjectMapper.findSubjectByNo(subject_no);
		log.info("subject:{}", subject); 
		model.addAttribute("subject", subject);

		// 페이징 처리한 전체 글
		List<QNA> qnas = qnaMapper.getAllQnas(rb, title_part, null, subject_no);
		log.info("qnas:{}", qnas);
		model.addAttribute("qnas", qnas);
		model.addAttribute("title_part", title_part);
		return "subject/qna/qna";
	}

	// 글쓰기 폼으로 이동
	@GetMapping("{subject_no}/qna/write")
	public String write(@PathVariable("subject_no") Long subject_no, Model model) {
		// DTO 처리
		model.addAttribute("writeQna", new QnaWriteForm(subject_no));
		return "subject/qna/writeQna";
	}

	// 글 등록
	@PostMapping("{subject_no}/qna/write")
	public String write(@PathVariable("subject_no") Long subject_no,
			@SessionAttribute(name = "loginMember", required = false) Member loginMember,
			@Validated @ModelAttribute("writeQna") QnaWriteForm write,
			BindingResult result) {
		if (result.hasErrors()) {
			return "subject/qna/writeQna";
		}

		// DTO를 Entity로 재변환
		write.setWriter(loginMember.getMember_id());
		QNA qna = write.toQNA(write);
		log.info("qna:{}", qna);
		qnaMapper.writeQna(qna);

		return "redirect:/subject/" + qna.getSubject_no() + "/qna";
	}

	// 질문글	읽기(qna_no를 찾아서 읽기)
	@GetMapping("{subject_no}/qna/read/{qna_no}")
	public String read(@SessionAttribute(name = "loginMember", required = false) Member loginMember,
			@RequestParam(defaultValue = "1") int page,
			@PathVariable Long subject_no, @PathVariable Long qna_no, Model model) {

		// 글을 읽기 위한 객체 생성
		QNA readQna = qnaMapper.findQnaByNo(qna_no);
		model.addAttribute("qna", readQna);
		int total = replyMapper.getTotal(qna_no);
		if (total <1) {
			total = 1;
		}

		// 댓글 페이징 처리를 위한 객체 생성
		PageNavigator navi = new PageNavigator(countPerPage, pagePerGroup, page, total);
		model.addAttribute("navi", navi);
		RowBounds rb = new RowBounds(navi.getStartRecord(), navi.getCountPerPage());
		List<Reply> replies = replyMapper.getAllReplies(rb, qna_no);

		model.addAttribute("replies", replies);
		log.info("qna :{}", readQna);
		log.info("replies :{}", replies);
		return "subject/qna/readQna";
	}

	// 글 수정하기 ( 페이지 이동 )
	@GetMapping("{subject_no}/qna/update/{qna_no}")
	public String update(@SessionAttribute(name = "loginMember", required = false) Member loginMember,
			@PathVariable Long subject_no, @PathVariable Long qna_no, Model model) {
		QNA qna = qnaMapper.findQnaByNo(qna_no);
		// 작성자 확인
		if (qna.getWriter().equals(loginMember.getMember_id())) {
			model.addAttribute("updateQna", qna);
			return "subject/qna/updateQna";
		}
		return "redirect:/subject/"+subject_no+"/qna";
	}

	// 글 수정하기 ( DB 수정 )
	@PostMapping("{subject_no}/qna/update/{qna_no}")
	public String update(@SessionAttribute(name = "loginMember", required = false) Member loginMember,
			@PathVariable Long subject_no, @PathVariable Long qna_no, Model model,
			@ModelAttribute("updateQna")QNA qna) {
		log.info("qna: {}", qna);
		QNA findQna = qnaMapper.findQnaByNo(qna.getQna_no());
		
		// 작성자 확인
		if (loginMember.getMember_id().equals(findQna.getWriter())) {
			findQna.setQna_title(qna.getQna_title());
			findQna.setQna_contents(qna.getQna_contents());
			qnaMapper.updateQna(findQna);
			log.info("update Success");
			return "redirect:/subject/"+subject_no+"/qna/read/"+qna_no;
		}
		log.info("updateFail");
		return "redirect:/subject/"+subject_no+"/qna/read/"+qna_no;
	}

	// 질문 글 삭제
	@PostMapping("{subject_no}/qna/delete/{qna_no}")
	public String deleteQna(@SessionAttribute(name = "loginMember", required = false) Member loginMember,
			@PathVariable Long subject_no, @PathVariable Long qna_no) {

		// 글 찾고 작성자가 맞으면 삭제 실행
		QNA findQna = qnaMapper.findQnaByNo(qna_no);
		if (findQna != null && loginMember.getMember_id().equals(findQna.getWriter())) {
			qnaMapper.deleteQna(qna_no);
		}
		return "redirect:/qna";
	}

}
