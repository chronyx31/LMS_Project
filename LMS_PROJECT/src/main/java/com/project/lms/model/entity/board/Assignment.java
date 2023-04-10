package com.project.lms.model.entity.board;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {
	private Long assignment_no;
	private Long subject_no;
	private String assignment_title;
	private String assignment_contents;
	private String writer;
	private LocalDateTime assignment_date;
	private String originalfile;
	private String savedfile;
	private Long score;
	
	public Assignment(Long subject_no, String assignment_title, String assignment_contents, String writer, 
					  String originalfile, String savedfile) {
		super();
		this.subject_no = subject_no;
		this.assignment_title = assignment_title;
		this.assignment_contents = assignment_contents;
		this.writer = writer;
		this.originalfile = originalfile;
		this.savedfile = savedfile;
	}

	
}
