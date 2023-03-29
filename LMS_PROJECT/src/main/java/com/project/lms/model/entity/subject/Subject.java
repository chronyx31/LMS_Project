package com.project.lms.model.entity.subject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Subject {
	private Long subject_no;
	private String subject_title;
	private Long category;
	private String teacher;
	private String subject_img;

	public Subject(String subject_title, Long category, String teacher, String subject_img) {
		super();
		this.subject_title = subject_title;
		this.category = category;
		this.teacher = teacher;
		this.subject_img = subject_img;
	}	

}
