package com.shk95.giteditor.core.user.application.port.in;

import com.shk95.giteditor.core.user.application.service.dto.MessageDto;
import com.shk95.giteditor.core.user.domain.user.UserId;

import java.util.List;

public interface GetMessageUseCase {

	List<MessageDto> getAllMyMessages(UserId myId);

	List<MessageDto> getMessagesFrom(UserId myId, UserId recipientId);
}