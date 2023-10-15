package com.shk95.giteditor.core.user.application.service;

import com.shk95.giteditor.core.user.application.port.in.GetMessageUseCase;
import com.shk95.giteditor.core.user.application.port.in.SendMessageUseCase;
import com.shk95.giteditor.core.user.application.port.out.MessageRepositoryPort;
import com.shk95.giteditor.core.user.application.port.out.UserCrudRepositoryPort;
import com.shk95.giteditor.core.user.application.service.dto.MessageDto;
import com.shk95.giteditor.core.user.domain.message.Message;
import com.shk95.giteditor.core.user.domain.user.User;
import com.shk95.giteditor.core.user.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageService implements SendMessageUseCase, GetMessageUseCase {

	private final MessageRepositoryPort messageRepositoryPort;
	private final UserCrudRepositoryPort userCrudRepositoryPort;

	@Transactional(readOnly = true)
	@Override
	public List<MessageDto> getAllMyMessages(UserId myId) {
		return userCrudRepositoryPort.findUserByUserId(myId)
			.map(messageRepositoryPort::findBySender)
			.map(messages -> messages.stream()
				.map(message -> new MessageDto(message.getSender().getUserId().getProviderType(),
					message.getSender().getUserId().getUserLoginId(),
					message.getRecipient().getUserId().getProviderType(),
					message.getRecipient().getUserId().getUserLoginId(),
					message.getContent(),
					message.getTimestamp()))
				.collect(Collectors.toList()))
			.orElseGet(ArrayList::new);
	}

	@Transactional(readOnly = true)
	@Override
	public List<MessageDto> getMessagesFrom(UserId myId, UserId recipientId) {
		Function<Message, MessageDto> toDto = message ->
			new MessageDto(
				message.getSender().getUserId().getProviderType(),
				message.getSender().getUserId().getUserLoginId(),
				message.getRecipient().getUserId().getProviderType(),
				message.getRecipient().getUserId().getUserLoginId(),
				message.getContent(),
				message.getTimestamp());

		Optional<User> me = userCrudRepositoryPort.findUserByUserId(myId);
		Optional<User> recipient = userCrudRepositoryPort.findUserByUserId(recipientId);
		if (me.isEmpty() || recipient.isEmpty()) {
			log.info("User not found. sender : [{}], recipient : [{}]", myId, recipientId);
			return new ArrayList<>();
		}
		List<MessageDto> sent = messageRepositoryPort.findBySenderAndRecipient(me.get(), recipient.get()).stream()
			.map(toDto).toList();
		List<MessageDto> received = messageRepositoryPort.findBySenderAndRecipient(recipient.get(), me.get()).stream()
			.map(toDto).toList();

		List<MessageDto> result = new ArrayList<>();
		result.addAll(sent);
		result.addAll(received);
		result.sort(Comparator.comparing(MessageDto::timestamp));
		return result;
	}

	@Transactional
	@Override
	public void sendMessage(UserId myId, UserId recipientId, String content) {
		Optional<User> me = userCrudRepositoryPort.findUserByUserId(myId);
		Optional<User> recipient = userCrudRepositoryPort.findUserByUserId(recipientId);
		if (me.isEmpty() || recipient.isEmpty()) {
			log.info("User not found. sender : [{}], recipient : [{}]", myId, recipientId);
			return;
		}
		messageRepositoryPort.saveMessage(me.get(), recipient.get(), content);
	}
}
