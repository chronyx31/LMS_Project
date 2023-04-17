package com.project.lms.model.dto.member;

import com.project.lms.model.entity.subject.Subject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberMyLecture {
	private Long subject_no;
	private String subject_title;
	private String subject_img;
	
	public Subject toSubject(MemberMyLecture myLec) {
		Subject subject = new Subject();
		subject.setSubject_no(myLec.getSubject_no());
		subject.setSubject_title(myLec.getSubject_title());
		subject.setSubject_img(myLec.getSubject_img());
		return subject;
	}
}
