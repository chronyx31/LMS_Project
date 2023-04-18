package com.project.lms.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.project.lms.model.entity.board.Lecture;

@Mapper
public interface LectureMapper {

	// 게시글 갯수 검색
	int getTotal(@Param(value = "title_part") String title_part, @Param(value = "subject_no") Long subject_no);

	// 게시글 전체 검색
	List<Lecture> getAllLectures(RowBounds rb, @Param(value = "title_part") String title_part,
			@Param(value = "subject_no") Long subject_no);

	// 게시글 검색
	Lecture findLectureByNo(Long lecture_no);
	//여기부터
	int getMyTotal(Long subject_no);
	List<Lecture> getAllMyLectures(Long subject_no);
	//여기까지
}
