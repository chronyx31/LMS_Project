package com.project.lms.repository;

import org.apache.ibatis.annotations.Mapper;

import com.project.lms.model.entity.member.Member;

@Mapper
public interface MemberMapper {
	// 회원가입
	int joinMember(Member member);
	// ID로 회원 찾기
	Member findMemberById(String member_id);
	// 멤버 객체로 회원 찾기 (?)
	Member findId(Member member);
	// 회원정보 수정
	int modifyMember(Member member);
}