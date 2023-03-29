package com.project.lms.model.entity.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {
	private Long attendance_no;
	private Long subject_no;
	private Long lecture_no;
	private String member_id;
	private Long video_point;
	private Long video_length;
	private String attend_check;

	public Attendance(Long lecture_no, String member_id) {
		super();
		this.lecture_no = lecture_no;
		this.member_id = member_id;
	}

}
