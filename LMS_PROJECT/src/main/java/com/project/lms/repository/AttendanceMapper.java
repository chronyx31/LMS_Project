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

	// 업데이트 하기
	int updateAttendance(Attendance attendance);

	// 이어보기 지점 저장
	int setContinuePoint(Attendance attendance);
}
