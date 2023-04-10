package com.project.lms.controller;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.multipart.MultipartFile;

import com.project.lms.model.dto.board.AssignmentWriteForm;
import com.project.lms.model.entity.board.Assignment;
import com.project.lms.model.entity.board.QNA;
import com.project.lms.model.entity.member.Member;
import com.project.lms.model.entity.subject.Subject;
import com.project.lms.model.util.AttachedFile;
import com.project.lms.repository.AssignmentMapper;
import com.project.lms.repository.SubjectMapper;
import com.project.lms.util.FileService;
import com.project.lms.util.PageNavigator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/subject")
public class AssignmentController {

	private final SubjectMapper subjectMapper;
	private final AssignmentMapper assignmentMapper;
	private final FileService fileService;
	
	final int countPerPage = 3;//한 페이지에 표시될 게시글 숫자
	final int pagePerGroup = 5;//한번에 표시될 페이지의 수
	
	@Value("${file.upload.path}")
	private String uploadPath;	// 업드로 했을 때 저장될 위치 지정
	
	
	
	// 기본경로
	@GetMapping("{subject_no}/assignment")
	public String goToAssign(@PathVariable Long subject_no, @RequestParam(defaultValue = "1") int page,
			  @RequestParam(required = false) String title_part, Model model) {
		log.info("title_part:{}", title_part);	// title_part는 검색용
		
		// 검색 조건 없을 때, null 출력. category 이름으로 총 갯수 검색
		int total = assignmentMapper.getTotal(title_part, null, subject_no);
		// 글이 없을 경우, total=0이되면 이상하니까 1로 처리.
		if (total < 1) {
			total = 1;
		}
		log.info("total:{}", total);
		
		// 페이징 처리를 위한 navigator 객체 생성
		PageNavigator navi = new PageNavigator(countPerPage, pagePerGroup, page, total);
		model.addAttribute("navi", navi);
		RowBounds rb = new RowBounds(navi.getStartRecord(), navi.getCountPerPage());

		Subject subject = subjectMapper.findSubjectByNo(subject_no);	// subject를 읽기 위해서 불러옴
		log.info("subject:{}", subject); 
		model.addAttribute("subject", subject);
		
		
		// 페이징 처리한 전체 글
		List<Assignment> assignment = assignmentMapper.getAllAssignment(rb, title_part, null, subject_no);
		log.info("assignment:{}", assignment);
		model.addAttribute("assignments", assignment);
		model.addAttribute("title_part", title_part);
		
		return "subject/assignment/assignment";
	}

	// 과제게시판 등록 폼으로 이동
		@GetMapping("{subject_no}/assignment/write")
		public String write(@PathVariable("subject_no") Long subject_no, Model model) {
			// 과목 받아오는 subject 객체 생성
			Subject subject = subjectMapper.findSubjectByNo(subject_no);
			model.addAttribute("subject", subject);
			// DTO 처리
			model.addAttribute("write", new AssignmentWriteForm(subject_no));
			return "subject/assignment/write";
		}
	
		// 글 등록
		@PostMapping("{subject_no}/assignment/write")
		public String write(@PathVariable("subject_no") Long subject_no,
				@SessionAttribute(name = "loginMember", required = false) Member loginMember,
				@Validated @ModelAttribute("write") AssignmentWriteForm write, BindingResult result,
				MultipartFile file) {
			if (result.hasErrors()) {
				return "subject/assignment/write";
			}
			
			Subject subject = subjectMapper.findSubjectByNo(subject_no);

			// 파일 업로드
			log.info("file:{}", file);
			log.info("file.getOriginalFileName():{}", file.getOriginalFilename());
			log.info("file.getSize():{}", file.getSize());

			AttachedFile savedFile = fileService.saveFile(file);
			log.info("savedFile:{}", savedFile);

		// DTO를 Entity로 변환
			write.setWriter(loginMember.getMember_id());
			Assignment assignment = write.toAssignment(write);
			log.info("assignment:{}", assignment);
			assignmentMapper.writeAssignment(assignment, savedFile);
			
			return "redirect:/subject/" + assignment.getSubject_no() + "/assignment";
		}	
	
	// 읽기(읽기에 다운로드 넣어야 할듯) -> 다운로드용 하나 만들기
		@GetMapping("{subject_no}/assignment/read/{assignment_no}")
		public String read(@SessionAttribute(name = "loginMember", required = false) Member loginMember,
				@RequestParam(defaultValue = "1") int page,
				@PathVariable Long subject_no, @PathVariable Long assignment_no, Model model) {

			// 글을 읽기 위한 객체 생성
			Subject subject = subjectMapper.findSubjectByNo(subject_no);
			Assignment readAssign = assignmentMapper.findAssignmentByNo(assignment_no);
			model.addAttribute("subject", subject);
			model.addAttribute("readAssign", readAssign);
			
		
			return "subject/assignment/read";
		}
	
		// 수정 페이지 이동
		@GetMapping("{subject_no}/assignment/update/{assignment_no}")
		public String update(@SessionAttribute(name = "loginMember", required = false) Member loginMember,
							 @PathVariable Long subject_no, @PathVariable Long assignment_no, Model model) {
			// 사이드바에서 과목명을 보여주기 위한 과목 객체 등록
			Subject subject = subjectMapper.findSubjectByNo(subject_no);
			model.addAttribute("subject", subject);
			Assignment updateAssign = assignmentMapper.findAssignmentByNo(assignment_no);
			
			// 작성자 = 로그인 멤버가 같은 경우 찾기
			if (updateAssign.getWriter().equals(loginMember.getMember_id())) {
				model.addAttribute("updateAssign", updateAssign);
				return "subject/assignment/update";
			}
			return "redirect:/subject/" + subject_no + "/assignment";
		}
	
			
		// DB 수정
		@PostMapping("{subject_no}/assignment/update/{assignment_no}")
		public String update(@SessionAttribute(name = "loginMember", required = false) Member loginMember,
				@PathVariable Long subject_no, @PathVariable Long assignment_no, Model model,
				@ModelAttribute("updateAssign")Assignment assignment) {
			
			log.info("assignment:{}", assignment);
			Assignment findAssignment = assignmentMapper.findAssignmentByNo(assignment.getAssignment_no());
			// 작성자 = 로그인 멤버가 같은 경우 set
			if (loginMember.getMember_id().equals(findAssignment.getWriter())) {
				findAssignment.setAssignment_title(assignment.getAssignment_title());
				findAssignment.setAssignment_contents(assignment.getAssignment_contents());
				assignmentMapper.updateAssignment(findAssignment);
				log.info("수정 성공");
				return "redirect:/subject/" + subject_no + "/assignment";
			}
				log.info("수정 실패했습니다.");
			
			return "redirect:/subject/" + subject_no + "/assignment/read/" + assignment_no;	// 수정 실패시 수정 전 페이지로
		}
	
		
		// 과제글 삭제
		@PostMapping("{subject_no}/assignment/delete/{assignment_no}")
		public String delete(@SessionAttribute(name = "loginMember", required = false) Member loginMember,
				@PathVariable Long subject_no, @PathVariable Long assignment_no) {

			// 로그인 멤버 = 작성자가 일치 할 시 삭제 시키기
			Assignment findAssignment = assignmentMapper.findAssignmentByNo(assignment_no);
			if (findAssignment != null && loginMember.getMember_id().equals(findAssignment.getWriter())) {
				assignmentMapper.deleteAssignment(assignment_no);
			}
			return "redirect:/subject/"+subject_no+"/assignment/";
		}
		
		
		
		
		
		
		
}

