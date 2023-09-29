package com.shk95.giteditor.core.openai.application.service;

import com.shk95.giteditor.core.openai.application.port.in.CreateCompletionUseCase;
import com.shk95.giteditor.core.openai.application.port.in.LoadMessagesUseCase;
import com.shk95.giteditor.core.openai.application.port.in.command.GetCompletionCommand;
import com.shk95.giteditor.core.openai.application.port.in.command.RequestCommand;
import com.shk95.giteditor.core.openai.application.port.out.GptApiPort;
import com.shk95.giteditor.core.openai.application.port.out.LoadMessagePort;
import com.shk95.giteditor.core.openai.application.port.out.SaveMessagePort;
import com.shk95.giteditor.core.openai.domain.ChatDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService implements LoadMessagesUseCase, CreateCompletionUseCase {

	private final GptApiPort gptService;
	private final SaveMessagePort saveMessagePort;
	private final LoadMessagePort loadMessagePort;

	@Override
	public ChatDocument createCompletion(RequestCommand command) {
		String prompt = command.prompt();
		String completion = gptService.getCompletion(command.accessToken(), command.prompt());
		return saveMessagePort.save(command.userId(), prompt, completion);
	}

	@Override
	public PageResult<ChatDocument> loadMessages(GetCompletionCommand command) {
		return loadMessagePort.findAllByUserIdOrderByCreatedDateDesc(
			command.userId(),
			command.pageAt(),
			command.size(),
			SortInfo.builder()
				.direction(SortInfo.Direction.DESC)
				.property("createdDate")
				.build());
	}
}
