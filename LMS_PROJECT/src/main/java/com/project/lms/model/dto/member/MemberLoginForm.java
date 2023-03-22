package com.project.lms.model.dto.member;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class MemberLoginForm {
	@Size(min = 4, max = 20)
	private String member_id;
	@NotBlank
	private String member_password;
}
