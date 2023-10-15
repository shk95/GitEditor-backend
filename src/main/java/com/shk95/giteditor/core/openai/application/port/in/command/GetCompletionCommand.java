package com.shk95.giteditor.core.openai.application.port.in.command;

import lombok.Builder;

@Builder
public record GetCompletionCommand(

	String userId,
	String accessToken,
	int pageAt,
	int size
) {

}
