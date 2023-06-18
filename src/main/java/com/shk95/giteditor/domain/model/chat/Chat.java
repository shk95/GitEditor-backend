package com.shk95.giteditor.domain.model.chat;

import com.shk95.giteditor.domain.model.user.UserId;
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
public class Chat {// TODO : 사용자 삭제시 채팅 삭제

	@Id
	private String id;

	private UserId userId;

	private String prompt;

	private String completion;
	@CreatedDate
	private LocalDateTime createdDate;
	@LastModifiedDate
	private LocalDateTime lastModifiedDate;
}
