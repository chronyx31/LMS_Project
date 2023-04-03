package com.project.lms.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.project.lms.model.entity.board.QNA;

@Mapper
public interface QnaMapper {
	
	// 전체 글 갯수 조회 ( 페이징 처리용 )
	int getTotal(@Param(value = "title_part") String title_part,
			@Param(value = "category_name") String category_name,
			@Param(value = "subject_no") Long subject_no);

	// 질문글 전체 조회 ( 검색기능, 카테고리 포함 )
	List<QNA> getAllQnas(RowBounds rb, @Param(value = "title_part") String title_part,
			@Param(value = "category_name") String category_name,
			@Param(value = "subject_no") Long subject_no);

	// 질문 글 찾기(읽기)
	QNA findQnaByNo(Long qna_no);

	// 글 작성
	int writeQna(QNA qna);

	// 글 수정
	int updateQna(QNA qna);

	// 글 삭제
	int deleteQna(Long qna_no);
}
