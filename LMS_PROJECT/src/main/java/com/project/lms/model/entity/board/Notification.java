package com.project.lms.model.entity.board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
	private Long notification_no;
	private Long subject_no;
	private String notification_title;
	private String notification_contents;
	private String writer;

	public Notification(Long subject_no, String notification_title, String notification_contents, String writer) {
		super();
		this.subject_no = subject_no;
		this.notification_title = notification_title;
		this.notification_contents = notification_contents;
		this.writer = writer;
	}
}
