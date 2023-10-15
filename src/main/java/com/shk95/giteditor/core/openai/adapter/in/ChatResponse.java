package com.shk95.giteditor.core.openai.adapter.in;

import com.shk95.giteditor.core.openai.application.service.PageResult;
import com.shk95.giteditor.core.openai.domain.ChatDocument;
import lombok.Builder;
import lombok.Getter;

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

		public static Messages of(PageResult<ChatDocument> messages) {

			return ChatResponse.Messages.builder()
				.isLast(messages.getPageInfo().isLast())
				.messages(messages.getItems().stream()
					.map(message -> Message.builder()
						.completion(message.getCompletion())
						.prompt(message.getPrompt())
						.createdDate(message.getCreatedDate()).build())
					.collect(Collectors.toList()))
				.totalSize(messages.getPageInfo().getTotalSize())
				.totalPage(messages.getPageInfo().getTotalPage()).build();
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
