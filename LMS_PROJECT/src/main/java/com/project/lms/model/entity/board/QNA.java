package com.project.lms.model.entity.board;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QNA {
	private Long qna_no;
	private Long subject_no;
	private String qna_title;
	private String qna_contents;
	private String writer;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime qna_date;

	public QNA(Long subject_no, String qna_title, String qna_contents, String writer) {
		super();
		this.subject_no = subject_no;
		this.qna_title = qna_title;
		this.qna_contents = qna_contents;
		this.writer = writer;
	}
}
