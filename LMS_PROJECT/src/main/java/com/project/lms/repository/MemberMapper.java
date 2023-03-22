package com.project.lms.repository;

import org.apache.ibatis.annotations.Mapper;

import com.project.lms.model.entity.member.Member;

@Mapper
public interface MemberMapper {
	int joinMember(Member member);
	Member findMemberById(String id);
	Member findId(Member member);
	int updateMember(Member member);
}