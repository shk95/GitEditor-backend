package com.shk95.giteditor.core.openai.application.port.in.command;

import lombok.Builder;

@Builder
public record UpdateOpenAIServiceCommand(
	String userId,
	String accessToken
) {

}
