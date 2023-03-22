package com.project.lms.model.dto.member;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.project.lms.model.entity.member.Member;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class MemberJoinForm {
	@NotBlank
	@Size(min = 4, max = 20)
	private String member_id;
	@Size(min = 4, max = 20)
	private String member_password;
	@NotBlank
	private String member_name;
	@NotBlank
	private String member_email;
	@NotBlank
	private String password_question;
	@NotBlank
	private String password_answer;
	
	public MemberJoinForm(String member_id, String member_password, String member_name,
			String member_email, String password_question, String password_answer) {
		super();
		this.member_id = member_id;
		this.member_password = member_password;
		this.member_name = member_name;
		this.member_email = member_email;
		this.password_question = password_question;
		this.password_answer = password_answer;
	}
	
	public static Member toMember(MemberJoinForm joinForm) {
		Member member = new Member();
		member.setMember_id(joinForm.getMember_id());
		member.setMember_password(joinForm.getMember_password());
		member.setMember_name(joinForm.getMember_name());
		member.setMember_email(joinForm.getMember_email());
		member.setPassword_question(joinForm.getPassword_question());
		member.setPassword_answer(joinForm.getPassword_answer());
		return member;
	}
}

