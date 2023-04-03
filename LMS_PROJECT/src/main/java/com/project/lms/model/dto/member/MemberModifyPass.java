package com.project.lms.model.dto.member;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.project.lms.model.entity.member.Member;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class MemberModifyPass {
	private Long member_no;
	@Size(min = 4, max = 20)
	private String member_password;
	@NotBlank
	private String password_question;
	@NotBlank
	private String password_answer;
	
	public Member toMember(MemberModifyPass modify) {
		Member member = new Member();
		member.setMember_no(modify.getMember_no());
		member.setMember_password(modify.getMember_password());
		member.setPassword_question(modify.getPassword_question());
		member.setPassword_answer(modify.getPassword_answer());
		return member;
	}
}
