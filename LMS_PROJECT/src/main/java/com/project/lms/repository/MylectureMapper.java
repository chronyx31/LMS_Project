package com.project.lms.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.project.lms.model.entity.member.MyLecture;

@Mapper
public interface MylectureMapper {
	// 내강의 정보 찾기
	MyLecture isMylectureExist(@Param("subject_no") Long subject_no, @Param("member_no") Long member_no);
}
