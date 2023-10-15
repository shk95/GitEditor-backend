package com.shk95.giteditor.core.user.application.port.out;

import com.shk95.giteditor.core.user.domain.message.Message;
import com.shk95.giteditor.core.user.domain.user.User;

import java.util.List;

public interface MessageRepositoryPort {

	List<Message> findBySender(User sender);

	List<Message> findBySenderAndRecipient(User sender, User recipient);

	void saveMessage(User sender, User recipient, String content);
}
