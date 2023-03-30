package com.project.lms.model.entity.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {
	private Long member_no;					// 회원번호
	private String member_id;				// 아이디
	private String member_password;			// 비밀번호
	private String member_name;				// 이름
	private String member_email;			// 이메일
	private String password_question;		// 비밀번호 찾기 질문
	private String password_answer;			// 비밀번호 찾기 답

	public Member(String member_id, String member_password, String member_name,
			String member_email, String password_question, String password_answer) {
		super();
		this.member_id = member_id;
		this.member_password = member_password;
		this.member_name = member_name;
		this.member_email = member_email;
		this.password_question = password_question;
		this.password_answer = password_answer;
	}

}
