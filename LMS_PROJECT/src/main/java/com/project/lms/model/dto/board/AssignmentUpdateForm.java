package com.project.lms.model.dto.board;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;

import org.springframework.format.annotation.DateTimeFormat;

import com.project.lms.model.entity.board.Assignment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssignmentUpdateForm {
	private Long subject_no;
	@NotBlank
	private String assignment_title;
	@NotBlank
	private String assignment_contents;
	@NotBlank
	private String originalfile;
	private String savedfile;		
//	@DateTimeFormat(pattern = "yyyy-MM-dd")
//	private LocalDateTime assignment_date;
	
	public AssignmentUpdateForm(Long subject_no) {
		this.subject_no = subject_no;
	}

	public AssignmentUpdateForm(String assignment_title, String assignment_contents, String originalfile) {
		super();
		this.assignment_title = assignment_title;
		this.assignment_contents = assignment_contents;
		this.originalfile = originalfile;
	}
	
	public Assignment toAssignment(AssignmentUpdateForm update) {
		Assignment assignment = new Assignment();
		assignment.setSubject_no(update.getSubject_no());
		assignment.setAssignment_title(update.getAssignment_title());
		assignment.setAssignment_contents(update.getAssignment_contents());
		assignment.setOriginalfile(update.getOriginalfile());
		return assignment;
	}
	
}
