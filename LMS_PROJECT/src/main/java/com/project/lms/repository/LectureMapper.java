package com.project.lms.repository;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LectureMapper {

	// 게시글 갯수 검색
	int getTotal(String title_part, Long subject_no);

}
