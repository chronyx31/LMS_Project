package com.project.lms.model.entity.board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lecture {
	private Long lecture_no;
	private Long subject_no;
	private String lecture_title;
	private String lecture_contents;
	private String writer;

	public Lecture(Long subject_no, String lecture_title, String lecture_contents, String writer) {
		super();
		this.subject_no = subject_no;
		this.lecture_title = lecture_title;
		this.lecture_contents = lecture_contents;
		this.writer = writer;
	}
}
