package com.project.lms.model.dto.board;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import com.project.lms.model.entity.board.Assignment;
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
	private Long score;
	private String originalfile;
	private String savedfile;
	private MultipartFile file;
	
	public AssignmentWriteForm(Long subject_no) {
		this.subject_no = subject_no;
	}

	public AssignmentWriteForm(String assignment_title, String assignment_contents, String writer, Long score, 
							   String originalfile, String savedfile) {
		super();
		this.assignment_title = assignment_title;
		this.assignment_contents = assignment_contents;
		this.writer = writer;
		this.score = score;
		this.originalfile = originalfile;
		this.savedfile = savedfile;
	}
	
	public Assignment toAssignment(AssignmentWriteForm write) {
		Assignment assignment = new Assignment();
		assignment.setSubject_no(write.getSubject_no());
		assignment.setAssignment_title(write.getAssignment_title());
		assignment.setAssignment_contents(write.getAssignment_contents());
		assignment.setWriter(write.getWriter());
		assignment.setScore(write.getScore());
		assignment.setOriginalfile(write.getOriginalfile());
		assignment.setSavedfile(write.getSavedfile());
		return assignment;
	}
	
}
