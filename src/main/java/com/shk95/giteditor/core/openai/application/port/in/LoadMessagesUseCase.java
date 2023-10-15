package com.shk95.giteditor.core.openai.application.port.in;

import com.shk95.giteditor.core.openai.application.port.in.command.GetCompletionCommand;
import com.shk95.giteditor.core.openai.application.service.PageResult;
import com.shk95.giteditor.core.openai.domain.ChatDocument;

public interface LoadMessagesUseCase {

	PageResult<ChatDocument> loadMessages(GetCompletionCommand getCompletionCommand);
}
