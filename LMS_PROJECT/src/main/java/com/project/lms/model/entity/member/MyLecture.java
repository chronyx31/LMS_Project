package com.project.lms.model.entity.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyLecture {
	private Long mylecture_no;
	private Long member_no;
	private Long subject_no;

	public MyLecture(Long member_no, Long subject_no) {
		super();
		this.member_no = member_no;
		this.subject_no = subject_no;
	}
}
