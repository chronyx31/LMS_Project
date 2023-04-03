package com.project.lms.repository;

import org.apache.ibatis.annotations.Mapper;

import com.project.lms.model.entity.member.Member;

@Mapper
public interface MemberMapper {
	int joinMember(Member member);
	Member findMemberById(String member_id);
	Member findId(Member member);
	int modifyMember(Member member);
}