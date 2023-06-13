package com.shk95.giteditor.domain.application.impl;

import com.shk95.giteditor.domain.application.ChatService;
import com.shk95.giteditor.domain.application.commands.chat.GetCompletionCommand;
import com.shk95.giteditor.domain.application.commands.chat.RequestCommand;
import com.shk95.giteditor.domain.model.chat.Chat;
import com.shk95.giteditor.domain.model.chat.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.shk95.giteditor.domain.model.chat.SimpleGpt.simpleResponse;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatServiceImpl implements ChatService {

	private final ChatRepository chatRepository;

	@Transactional
	public Chat simpleRequest(RequestCommand command) {
		String prompt = command.getPrompt();
		String completion = simpleResponse(command.getAccessToken(), command.getPrompt());
		return chatRepository.save(Chat.builder()
			.userId(command.getUserId())
			.prompt(prompt)
			.completion(completion).build());
	}

	@Transactional(readOnly = true)
	public Page<Chat> getCompletions(GetCompletionCommand command) {
		PageRequest pageRequest = PageRequest.of(command.getPageAt(), command.getSize(), Sort.by(Sort.Direction.DESC, "createdDate"));
		return chatRepository.findAllByUserIdOrderByCreatedDateDesc(command.getUserId(),pageRequest);
	}
}
