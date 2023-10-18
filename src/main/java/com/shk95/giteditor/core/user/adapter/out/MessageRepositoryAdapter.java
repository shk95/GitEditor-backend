package com.shk95.giteditor.core.user.adapter.out;

import com.shk95.giteditor.core.user.application.port.out.MessageRepositoryPort;
import com.shk95.giteditor.core.user.application.port.out.UserCrudRepositoryPort;
import com.shk95.giteditor.core.user.domain.message.Message;
import com.shk95.giteditor.core.user.domain.user.User;
import com.shk95.giteditor.core.user.domain.user.UserId;
import com.shk95.giteditor.core.user.infrastructure.JpaMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class MessageRepositoryAdapter implements MessageRepositoryPort {

	private final UserCrudRepositoryPort userCrudRepositoryPort;
	private final JpaMessageRepository jpaMessageRepository;

	@Override
	public int countUnreadMessage(UserId userId) {
		return userCrudRepositoryPort.findUserByUserId(userId)
			.map(user -> jpaMessageRepository.countByRecipientAndRead(user, false))
			.orElse(0);
	}

	@Override
	public List<Message> getUnreadMessages(UserId recipientId) {
		return userCrudRepositoryPort.findUserByUserId(recipientId)
			.map(recipient -> jpaMessageRepository.findByRecipientAndReadOrderByTimestampAsc(recipient, false))
			.orElseGet(ArrayList::new);
	}

	@Override
	public List<Message> findBySender(User sender) {
		return jpaMessageRepository.findBySender(sender);
	}

	@Override
	public List<Message> findBySenderAndRecipient(User sender, User recipient) {
		return jpaMessageRepository.findBySenderAndRecipient(sender, recipient);
	}

	@Override
	public void saveMessage(User sender, User recipient, String content) {
		Message message = Message.builder()
			.sender(sender)
			.recipient(recipient)
			.content(content)
			.build();
		jpaMessageRepository.save(message);
	}
}
