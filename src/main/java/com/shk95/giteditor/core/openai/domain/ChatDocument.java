package com.shk95.giteditor.core.openai.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@AllArgsConstructor
@Getter
@Builder
@Document(collection = "chat")
public class ChatDocument {// TODO : 사용자 삭제시 채팅 삭제

	@Id
	private String id;

	private String userId;

	private String prompt;

	private String completion;
	@CreatedDate
	private LocalDateTime createdDate;
	@LastModifiedDate
	private LocalDateTime lastModifiedDate;

	@Override
	public String toString() {
		return "ChatDocument{" +
			"id='" + id + '\'' +
			", userId='" + userId + '\'' +
			", prompt='" + prompt + '\'' +
			", completion='" + completion + '\'' +
			", createdDate=" + createdDate +
			", lastModifiedDate=" + lastModifiedDate +
			'}';
	}
}
