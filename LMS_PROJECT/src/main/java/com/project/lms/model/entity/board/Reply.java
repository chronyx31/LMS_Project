package com.project.lms.model.entity.board;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reply {
	private Long reply_no;
	private Long qna_no;
	private String reply_contents;
	private String writer;
	private LocalDateTime reply_date;

	public Reply(Long qna_no, String reply_contents, String writer) {
		super();
		this.qna_no = qna_no;
		this.reply_contents = reply_contents;
		this.writer = writer;
	}
}
