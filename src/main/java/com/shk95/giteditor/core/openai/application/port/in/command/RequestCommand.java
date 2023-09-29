package com.shk95.giteditor.core.openai.application.port.in.command;

import lombok.Builder;

@Builder
public record RequestCommand(
	String userId,
	String accessToken,
	String prompt
) {

}
