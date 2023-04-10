package com.project.lms.model.dto.board;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;

import org.springframework.format.annotation.DateTimeFormat;

import com.project.lms.model.entity.board.Assignment;
import com.project.lms.model.entity.board.QNA;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssignmentWriteForm {
	private Long subject_no;
	@NotBlank
	private String assignment_title;
	@NotBlank
	private String assignment_contents;
	private String writer;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime assignment_date;
	
	public AssignmentWriteForm(Long subject_no) {
		this.subject_no = subject_no;
	}

	public AssignmentWriteForm(String assignment_title, String assignment_contents, String writer) {
		super();
		this.assignment_title = assignment_title;
		this.assignment_contents = assignment_contents;
		this.writer = writer;
	}
	
	public Assignment toAssignment(AssignmentWriteForm write) {
		Assignment assignment = new Assignment();
		assignment.setSubject_no(write.getSubject_no());
		assignment.setAssignment_title(write.getAssignment_title());
		assignment.setAssignment_contents(write.getAssignment_contents());
		assignment.setWriter(write.getWriter());
		return assignment;
	}
	
}
