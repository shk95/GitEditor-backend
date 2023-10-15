package com.shk95.giteditor.core.user.application.port.in;

import com.shk95.giteditor.core.user.domain.user.UserId;

public interface SendMessageUseCase {

	void sendMessage(UserId myId, UserId recipientId, String content);
}
