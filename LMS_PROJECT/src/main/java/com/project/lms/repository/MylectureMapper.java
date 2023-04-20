package com.project.lms.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.project.lms.model.dto.member.MemberMyLecture;
import com.project.lms.model.entity.member.Attendance;
import com.project.lms.model.entity.member.MyLecture;
import com.project.lms.model.entity.subject.Subject;

@Mapper
public interface MylectureMapper {
	// 내강의 정보 찾기
	MyLecture isMylectureExist(@Param("subject_no") Long subject_no, @Param("member_no") Long member_no);
	// 수강신청하기
	int ApplyLecture(MyLecture mylecture);
	// 내가 수강중인 강의 목록 가져오기
	List<MemberMyLecture> getAllMyLecture(RowBounds rb, Long member_no);
	// 내가 수강중인 강의 숫자 측정하기
	int getTotalofMyLecture(Long member_no);
	// 내 출석정보 가져오기
	List<Attendance> getMyAttendance (@Param("subject_no") Long subject_no, @Param("member_id") String member_id);
	
	String getAttendanceLecture_title (long lecture_no);
	String getAttendanceSubject_title (long subject_no);
}
