package com.project.lms.model.dto.member;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.project.lms.model.entity.member.Member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfoForm {
	@NotBlank
	private Long member_no;
	@NotBlank
	@Size(min = 4, max = 20)
	private String member_id;
	@NotBlank
	private String member_name;
	@NotBlank
	private String member_email;
	
	public MemberInfoForm(Member member) {
		this.member_no = member.getMember_no();
		this.member_id = member.getMember_id();
		this.member_name = member.getMember_name();
		this.member_email = member.getMember_email();
	}
}
