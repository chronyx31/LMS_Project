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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.project.lms.model.dto.board.QnaWriteForm;
import com.project.lms.model.entity.board.Notification;
import com.project.lms.model.entity.board.QNA;
import com.project.lms.model.entity.board.Reply;
import com.project.lms.model.entity.member.Member;
import com.project.lms.repository.NotificationMapper;
import com.project.lms.repository.QnaMapper;
import com.project.lms.repository.ReplyMapper;
import com.project.lms.util.PageNavigator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MainController {
	private final NotificationMapper notificationMapper;
	private final QnaMapper qnaMapper;
	private final ReplyMapper replyMapper;
	
	final int countPerPage = 2;//한 페이지에 표시될 게시글 숫자
	final int pagePerGroup = 5;//한번에 표시될 페이지의 수
	String category_name = "MAIN";
	Long subject_no = (long) 1;
	
	// 게시판 보기
	@GetMapping("notification")
	public String boardList(@RequestParam(defaultValue = "1") int page,
			@RequestParam(required = false) String title_part, Model model) {
		log.info(title_part);
		log.info("category_name: {}", category_name);
		// 검색조건 없으면 title_part는 null이 나온다. 카테고리 이름으로 총 갯수 검색
		// subject_no를 넣어서 과목을 특정한다. "MAIN"인 과목은 하나 밖에 안나오므로 null이라도 문제 없다.
		int total = notificationMapper.getTotal(title_part, category_name, null);
		// 글이 없을 경우 total이 0으로 나오므로 페이지 카운트가 이상하게 나오기 때문에 total을 1로 만들어 처리를 하거나
		// 0일경우 페이지 선택이 안나오도록 html에서 처리할 필요가 있다고 봄.
		if (total < 1) {
			total = 1;
		}
		log.info("total:{}", total);
		// 페이징 처리를 위한 navigator 객체 생성
		PageNavigator navi = new PageNavigator(countPerPage, pagePerGroup, page, total);
		model.addAttribute("navi", navi);
		RowBounds rb = new RowBounds(navi.getStartRecord(), navi.getCountPerPage());
		// 페이징 처리를 적용하여 전체 글 가져오기
		List<Notification> notifications = notificationMapper.getAllNotifications(rb, title_part, category_name, null);
		log.info("notifications:{}", notifications);
		model.addAttribute("notifications", notifications);
		// 공지사항 카테고리를 고정하기 위한 속성
		model.addAttribute("category_name", category_name);
		// 페이징 처리시 검색결과를 고정하기 위한 속성
		model.addAttribute("title_part", title_part);
		return "main/notification";
	}

	// 게시판 읽기
	@GetMapping("readnotice/{notification_no}")
	public String readNotification(@PathVariable Long notification_no, Model model) {
		// 만약 notification_no 가 문자면 NumberFormatException ( 400 에러 )
		// 만약 notification_no 가 없는 숫자면 NullPointerException ( 500 에러 )
		// 그러므로 문자열이나 없는 숫자의 잘못된 접근으로 인한 오류는 error로 페이지를 처리할 수 있음
		Notification notification = notificationMapper.findNotificationByNo(notification_no);
		model.addAttribute("notification", notification);
		return "main/readnotification";
	}


	// 질문게시판 읽기
	@GetMapping("qna")
	public String qnaList(@RequestParam(defaultValue = "1") int page, 
			@RequestParam(required = false) String title_part, Model model) {
		int total = qnaMapper.getTotal(title_part, category_name, null);
		if (total < 1) {
			total = 1;
		}

		// 페이징 처리를 위한 객체 생성
		PageNavigator navi = new PageNavigator(countPerPage, pagePerGroup, page, total);
		model.addAttribute("navi", navi);
		RowBounds rb = new RowBounds(navi.getStartRecord(), navi.getCountPerPage());
		List<QNA> qnas = qnaMapper.getAllQnas(rb, title_part, category_name, null);
		model.addAttribute("qnas", qnas);
		model.addAttribute("category_name", category_name);
		model.addAttribute("title_part", title_part);

		return "main/qna";
	}

	// 질문 글 읽기
	@GetMapping("readqna/{qna_no}")
	public String readQna(@RequestParam(defaultValue = "1") int page,
			@PathVariable Long qna_no, Model model) {
		// 만약 qna_no 가 문자면 NumberFormatException ( 400 에러 )
		// 만약 qna_no 가 없는 숫자면 NullPointerException ( 500 에러 )
		// 그러므로 문자열이나 없는 숫자의 잘못된 접근으로 인한 오류는 error로 페이지를 처리할 수 있음
		
		// 글을 읽기 위한 객체 생성
		QNA qna = qnaMapper.findQnaByNo(qna_no);
		model.addAttribute("qna", qna);
		int total = replyMapper.getTotal(qna_no);
		if (total < 1) {
			total = 1;
		}

		// 댓글 페이징 처리를 위한 객체 생성
		PageNavigator navi = new PageNavigator(countPerPage, pagePerGroup, page, total);
		model.addAttribute("navi", navi);
		RowBounds rb = new RowBounds(navi.getStartRecord(), navi.getCountPerPage());
		List<Reply> replies = replyMapper.getAllReplies(rb, qna_no);
		model.addAttribute("replies", replies);
		return "main/readqna";
	}

	// 질문 글 쓰기 ( 페이지 이동 )
	@GetMapping("writeQna")
	public String toWriteQna(Model model) {
		model.addAttribute("writeQna", new QnaWriteForm());
		return "main/writeqna";
	}

	// 질문 글 쓰기
	@PostMapping("writeQna")
	public String writeQna(@SessionAttribute(name = "loginMember", required = false) Member loginMember,
			@Validated @ModelAttribute("writeQna") QnaWriteForm write, BindingResult result) {
		if (result.hasErrors()) {
			return "main/writeqna";
		}

		// 과목번호와 작성자 추가 입력
		write.setWriter(loginMember.getMember_id());
		write.setSubject_no(subject_no);

		// DTO 재변환
		QNA qna = write.toQNA(write);
		// 글 작성 SQL
		qnaMapper.writeQna(qna);
		return"redirect:/qna";
	}

	// 질문 글 수정 ( 페이지 이동 )
	@GetMapping("updateQna/{qna_no}")
	public String update(@SessionAttribute(name = "loginMember", required = false) Member loginMember,
			@PathVariable Long qna_no, Model model) {
		QNA qna = qnaMapper.findQnaByNo(qna_no);
		if (qna.getWriter().equals(loginMember.getMember_id())) {
			model.addAttribute("qna", qna);
			log.info("updateQna이동");
			return "updateqna";
		}
		return "redirect:/qna";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

	// 질문 글 수정
	@PostMapping("updateQna/{qna_no}")
	public String updateQna(@SessionAttribute(name = "loginMember", required = false) Member loginMember,
			@PathVariable("qna_no") Long qna_no,
			@ModelAttribute QNA qna,
			Model model) {
		log.info("updateQna 실행");
		QNA findQna = qnaMapper.findQnaByNo(qna_no);
		
		if (loginMember.getMember_id().equals(findQna.getWriter())) {
			log.info("updateQna의 if절 실행");
			findQna.setQna_title(qna.getQna_title());
			findQna.setQna_contents(qna.getQna_contents());
			qnaMapper.updateQna(findQna);
			log.info("updateQna의 Mapper완료");
			return "redirect:/readqna/" + qna_no;
		}
	
	return "redirect:/qna";
	}

	// 질문 글 삭제
	@PostMapping("deleteQna/{qna_no}")
	public String deleteQna(@SessionAttribute(name = "loginMember", required = false) Member loginMember,
			@PathVariable Long qna_no) {
		log.info("삭제 메소드 실행");
		QNA findQna = qnaMapper.findQnaByNo(qna_no);
		if (findQna != null && loginMember.getMember_id().equals(findQna.getWriter())) {
			qnaMapper.deleteQna(qna_no);
			log.info("삭제 메소드의 if절 실행");
		}
		return "redirect:/qna";
	}
}
