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
	private Double video_point;
	private Double video_length;
	private String attend_check;
	private Long accumulate_time;

	public Attendance(Long subject_no, Long lecture_no, String member_id, Double video_length) {
		super();
		this.subject_no = subject_no;
		this.lecture_no = lecture_no;
		this.member_id = member_id;
		this.video_length = video_length;
	}

}
