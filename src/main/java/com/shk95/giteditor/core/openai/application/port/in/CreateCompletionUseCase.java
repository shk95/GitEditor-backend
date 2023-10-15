package com.shk95.giteditor.core.openai.application.port.in;

import com.shk95.giteditor.core.openai.domain.ChatDocument;
import com.shk95.giteditor.core.openai.application.port.in.command.RequestCommand;

public interface CreateCompletionUseCase {

	ChatDocument createCompletion(RequestCommand requestCommand);
}
