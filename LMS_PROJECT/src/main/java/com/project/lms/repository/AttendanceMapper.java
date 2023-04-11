package com.project.lms.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.project.lms.model.entity.member.Attendance;

@Mapper
public interface AttendanceMapper {

	// 출석체크 테이블 찾기
	Attendance findAttendance(@Param("lecture_no") Long lecture_no, @Param("member_id") String member_id);

	// 출석체크 테이블 만들기
	int createAttendance(Attendance createAttendance);
	
}
