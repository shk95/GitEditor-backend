package com.shk95.giteditor.web.apis.response;

import com.shk95.giteditor.domain.model.chat.Chat;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ChatResponse {

	@Getter
	@Builder
	public static class Messages {

		private List<Message> messages;
		private boolean isLast;
		private long totalSize;
		private int totalPage;

		public static Messages of(Page<Chat> messages) {

			return ChatResponse.Messages.builder()
				.isLast(messages.isLast())
				.messages(messages.getContent().stream().map(message -> Message.builder()
					.completion(message.getCompletion())
					.prompt(message.getPrompt())
					.createdDate(message.getCreatedDate()).build()).collect(Collectors.toList())
				)
				.totalSize(messages.getTotalElements())
				.totalPage(messages.getTotalPages()).build();
		}
	}

	@Getter
	@Builder
	public static class Message {

		private String prompt;
		private String completion;
		private LocalDateTime createdDate;
	}
}
