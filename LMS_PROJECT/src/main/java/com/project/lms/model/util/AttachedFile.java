package com.project.lms.model.util;

import lombok.Data;

@Data
public class AttachedFile {
	private Long file_id;
	private String originalFile;
	private String savedFile;
	
	public AttachedFile(String originalFile, String savedFile) {
		this.originalFile = originalFile;
		this.savedFile = savedFile;
	}
}

// 브라우저에서 선택한 파일 명 A.txt (originalFile)
// -> 서버로 전송되어 저장될 파일 명 20230221111023.txt (savedFile)
// 서버로 저장될 때 중복으로 인한 기존파일이 덮어씌워져 삭제되면 안된다.
// 다운로드 할 때는 원래 파일 명으로 저장되어야 사용자친화적이다.
