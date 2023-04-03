package com.project.lms.model.dto.board;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;

import org.springframework.format.annotation.DateTimeFormat;

import com.project.lms.model.entity.board.QNA;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QnaWriteForm {
	private Long subject_no;
	@NotBlank
	private String qna_title;
	@NotBlank
	private String qna_contents;
	private String writer;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime qna_date;
	
	public QnaWriteForm(Long subject_no) {
		this.subject_no = subject_no;
	}

	public QNA toQNA(QnaWriteForm write) {
		QNA qna = new QNA();
		qna.setSubject_no(write.getSubject_no());
		qna.setQna_title(write.getQna_title());
		qna.setQna_contents(write.getQna_contents());
		qna.setWriter(write.getWriter());
		return qna;
	}

	public QnaWriteForm(String qna_title, String qna_contents, String writer) {
		super();
		this.qna_title = qna_title;
		this.qna_contents = qna_contents;
		this.writer = writer;
	}
	
	
}
