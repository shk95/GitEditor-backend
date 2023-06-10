package com.shk95.giteditor.domain.application;

import com.shk95.giteditor.domain.application.commands.chat.GetCompletionCommand;
import com.shk95.giteditor.domain.application.commands.chat.RequestCommand;
import com.shk95.giteditor.domain.model.chat.Chat;
import org.springframework.data.domain.Page;

public interface ChatService {

	Chat simpleRequest(RequestCommand command);

	Page<Chat> getCompletions(GetCompletionCommand command);
}
