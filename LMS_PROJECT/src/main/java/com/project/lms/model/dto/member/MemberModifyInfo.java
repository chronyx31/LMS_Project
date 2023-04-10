package com.project.lms.model.dto.member;

import javax.validation.constraints.NotBlank;

import com.project.lms.model.entity.member.Member;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class MemberModifyInfo {
	private Long member_no;
	private String member_id;
	@NotBlank
	private String member_name;
	@NotBlank
	private String member_email;
	
	public Member toMember(MemberModifyInfo modify) {
		Member member = new Member();
		member.setMember_no(modify.getMember_no());
		member.setMember_id(modify.getMember_id());
		member.setMember_name(modify.getMember_name());
		member.setMember_email(modify.getMember_email());
		return member;
	}

}
