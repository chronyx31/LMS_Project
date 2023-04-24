package com.project.lms.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.parser.Entity;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.util.UriUtils;

import com.project.lms.model.dto.board.AssignmentUpdateForm;
import com.project.lms.model.dto.board.AssignmentWriteForm;
import com.project.lms.model.entity.board.Assignment;
import com.project.lms.model.entity.member.Member;
import com.project.lms.model.entity.member.MyLecture;
import com.project.lms.model.entity.subject.Subject;
import com.project.lms.model.util.AttachedFile;
import com.project.lms.repository.AssignmentMapper;
import com.project.lms.repository.MylectureMapper;
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
	private final MylectureMapper mylectureMapper;
	
	final int countPerPage = 5;//한 페이지에 표시될 게시글 숫자
	final int pagePerGroup = 5;//한번에 표시될 페이지의 수
	
	@Value("${file.upload.path}")
	private String uploadPath;	// 업드로 했을 때 저장될 위치 지정
	
	// 인터셉터 대신 사용될 코드 Controller 내 메소드 실행전 실행된다.
	// 수강신청을 했는지 확인하는 절차. 수강신청을 하지 않으면 강의를 볼 수 없다. 강의페이지에 들어오지 않도록 한다.
	@ModelAttribute("applyLecture")
	public MyLecture checkLecture(@PathVariable Long subject_no,
								  @SessionAttribute(name = "loginMember", required = false) Member loginMember, 
								  HttpServletResponse response) throws IOException {
		log.info("pathsub: {}", subject_no);
		log.info("member: {}", loginMember);

		// 내강의에 정보가 있는지 없는지 확인하는 Query문
		MyLecture isMylectureExist = mylectureMapper.isMylectureExist(subject_no, loginMember.getMember_no());
		log.info("isExist:{}", isMylectureExist);
		if (isMylectureExist == null) {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>alert('수강신청을 먼저 해주시기 바랍니다.'); location.href='/subject/" + subject_no
					+ "/notification';</script>");
			out.flush();
		}
		return isMylectureExist;
	}
	
	// 기본경로
	@GetMapping("{subject_no}/assignment")
	public String goToAssign(@PathVariable Long subject_no, 
							 @RequestParam(defaultValue = "1") int page,
							 @RequestParam(required = false) String title_part, Model model) {
		// title_part는 검색용
		log.info("title_part:{}", title_part);	
		
		// 검색 조건 없을 때, null 출력. category 이름으로 총 갯수 검색
		int total = assignmentMapper.getTotal(title_part, subject_no);
		// 글이 없을 경우, total=0 이되면 이상하니까 1로 처리.
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
		List<Assignment> assignment = assignmentMapper.getAllAssignment(rb, title_part, subject_no);
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
			@RequestParam(name="file", required = false) MultipartFile file, Model model) throws IOException {
		
		// 유효성 검사
			// 사이드바에 있는 과목을 받아옴
		if (result.hasErrors()) {
			Subject subject = subjectMapper.findSubjectByNo(subject_no);
			model.addAttribute("subject", subject);
			return "subject/assignment/write";
		}
		
		// 파일 업로드
		AttachedFile savedFile = fileService.saveFile(file);
		Subject subject = subjectMapper.findSubjectByNo(subject_no);

		// 파일을 등록하지 않으면 리스트로 리턴처리
		if(file.isEmpty()) {
			log.info("file:{}", file);
			log.info("file.getOriginalFileName():{}", file.getOriginalFilename());
			log.info("file.getSize():{}", file.getSize());		
			return "redirect:/subject/" + subject_no + "/assignment";
		} else {			
			//DTO를 Entity로 변환
			write.setWriter(loginMember.getMember_id());
			Assignment assignment = write.toAssignment(write);
			assignment.setOriginalfile(savedFile.getOriginalfile()); // 업로드 했던 것을 담아줘야 한다.
			assignment.setSavedfile(savedFile.getSavedfile());
			assignmentMapper.writeAssignment(assignment);
			log.info("assignment:{}", assignment);
		}
		return "redirect:/subject/" + subject_no + "/assignment";
	}

	// 읽기 페이지 이동
	@GetMapping("{subject_no}/assignment/read/{assignment_no}")
	public String read(@SessionAttribute(name = "loginMember", required = false) Member loginMember,
			@RequestParam(defaultValue = "1") int page, @PathVariable Long subject_no, @PathVariable Long assignment_no,
			@RequestParam(required = false) String title_part, Model model) {

		// 글을 읽기 위한 객체 생성
		Subject subject = subjectMapper.findSubjectByNo(subject_no);
		Assignment readAssign = assignmentMapper.findAssignmentByNo(assignment_no);
		model.addAttribute("subject", subject);
		model.addAttribute("readAssign", readAssign);

		// 파일을 읽기 위한 객체 생성
		List<Assignment> files = assignmentMapper.downloadFile(assignment_no);
		log.info("assignment_no:{}", assignment_no);
		log.info("subject", subject);
		model.addAttribute("files", files);

		return "subject/assignment/read";
	}

	// 읽기 - 다운로드
	@GetMapping("{subject_no}/assignment/read/{assignment_no}/download")
	public ResponseEntity<Resource> read(@PathVariable Long subject_no, @PathVariable Long assignment_no, Model model)
			throws MalformedURLException {

		Assignment readAssign = assignmentMapper.findAssignmentByNo(assignment_no);
		model.addAttribute("readAssign", readAssign);

		String fullPath = uploadPath + readAssign.getSavedfile();
		log.info("fullPath:{}", fullPath);

		// 바디에 넣어줄 값 세팅(file: 서버에 저장된 파일의 경로)
		UrlResource resource = new UrlResource("file:" + fullPath); // 경로 정보

		// 헤더에 넣어줄 값 세팅 (attatched; filename="다운로드 받을 파일명")
		String encodingFileName = UriUtils.encode(readAssign.getOriginalfile(), StandardCharsets.UTF_8); // 한글 인식 되도록 하게
																											// 하는...
		String contentDisposition = "attached; filename =\"" + encodingFileName + "\"";

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition).body(resource);
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
				@Validated @ModelAttribute("updateAssign") Assignment assignment, MultipartFile file, BindingResult result) {

			if (result.hasErrors()) {
				return "subject/assignment/update";
			}
			
			// 파일을 등록하지 않으면 과제게시판으로 리턴되도록 처리.
			if (file.isEmpty()) {
				return "redirect:/subject/" + subject_no + "/assignment";
			}
		
			Assignment findAssignment = assignmentMapper.findAssignmentByNo(assignment.getAssignment_no());
			AttachedFile savedFile = fileService.saveFile(file);
			
			// 작성자 = 로그인 멤버가 같은 경우 set
			if (loginMember.getMember_id().equals(findAssignment.getWriter())) {
				findAssignment.setAssignment_title(assignment.getAssignment_title());
				findAssignment.setAssignment_contents(assignment.getAssignment_contents());
				findAssignment.setScore(assignment.getScore());
				findAssignment.setOriginalfile(savedFile.getOriginalfile());
				findAssignment.setSavedfile(savedFile.getSavedfile());
				assignmentMapper.updateAssignment(findAssignment);
				log.info("수정 성공");
				return "redirect:/subject/" + subject_no + "/assignment";
			}
			
			// 수정 중에 파일이 업로드 되지 않았을 때
			if(!file.isEmpty()) {
				assignment.setSavedfile(savedFile.getSavedfile());	
				assignment.setOriginalfile(savedFile.getOriginalfile()); // 업로드 했던 것을 담아줘야 한다.			
				assignmentMapper.writeAssignment(assignment);
				return "subject/assignment/update";
			}

			return "redirect:/subject/" + subject_no + "/assignment/read/" + assignment_no; // 수정 실패시 수정 전 페이지로
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
		return "redirect:/subject/" + subject_no + "/assignment/";
	}

}

