package com.project.lms.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.project.lms.model.entity.board.Reply;

@Mapper
public interface ReplyMapper {
	int getTotal(@Param(value = "qna_no") Long qna_no);
	List<Reply> getAllReplies(RowBounds rb, @Param(value = "qna_no") Long qna_no);
}
