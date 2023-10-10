package com.shk95.giteditor.core.openai.application.port.in.command;

import com.shk95.giteditor.core.user.domain.user.UserId;
import lombok.Builder;

@Builder
public record UpdateOpenAIServiceCommand(
	UserId userId,
	String accessToken
) {

}
