package com.project.lms.model.entity.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Teacher {
	private Long teacher_no;
	private String teacher_id;
	private String teacher_password;
	private String teacher_name;

	public Teacher(String teacher_id, String teacher_password, String teacher_name) {
		super();
		this.teacher_id = teacher_id;
		this.teacher_password = teacher_password;
		this.teacher_name = teacher_name;
	}	
}
