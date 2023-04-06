package com.project.lms.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import com.project.lms.model.entity.board.Lecture;

@Mapper
public interface LectureMapper {

	// 게시글 갯수 검색
	int getTotal(String title_part, Long subject_no);

	// 게시글 검색
	List<Lecture> getAllLectures(RowBounds rb, String title_part, Long subject_no);
}
