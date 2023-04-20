package com.project.lms.model.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberAttendance {
	private String member_id;
	private String lecture_title;
	private String subject_title;
	private String attend_check;
}
