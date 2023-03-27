package com.project.lms.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.project.lms.model.entity.board.Notification;

@Mapper
public interface NotificationMapper {
	
	// 전체 글 갯수 조회 ( 페이징 처리용 )
	int getTotal(@Param(value = "title_part") String title_part, @Param(value = "category_name") String category_name);
	// 공지사항글 전체 조회 ( 검색기능, 카테고리 포함 )
	List<Notification> getAllNotifications(RowBounds rb, @Param(value = "title_part") String title_part,
			@Param(value = "category_name") String category_name);
	// 게시글 읽기
	Notification findNotificationByNo(Long notification_no);
}
