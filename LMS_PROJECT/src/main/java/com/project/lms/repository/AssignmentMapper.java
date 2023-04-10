package com.project.lms.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.project.lms.model.entity.board.Assignment;
import com.project.lms.model.util.AttachedFile;

@Mapper
public interface AssignmentMapper {
	
	// 전체 글 갯수 조회 ( 페이징 처리용 )
	int getTotal(@Param(value = "title_part") String title_part, 
				 @Param(value = "category_name") String category_name,
				 @Param(value = "subject_no") Long subject_no);

	// 과제글 전체 조회 ( 검색기능, 카테고리 포함 )
	List<Assignment> getAllAssignment(RowBounds rb, @Param(value = "title_part") String title_part,
									   @Param(value = "category_name") String category_name, 
									   @Param(value = "subject_no") Long subject_no);

	// 과제 글 찾기(읽기)
	Assignment findAssignmentByNo(Long assignment_no);

	// 글 작성
	int writeAssignment(Assignment assignment, AttachedFile file);

	// 글 수정
	int updateAssignment(Assignment assignment);

	// 글 삭제
	int deleteAssignment(Long assignment_no);
	
	// 전체 글 조회(파일 업로드, 다운로드 파일 따로)
	List<Assignment> getAllAssignmentFile(@Param(value = "category_name") String category_name, 
									   @Param(value = "subject_no") Long subject_no);
//	
//	int saveFile(AttachedFile file);
}
