package com.shk95.giteditor.core.auth.application.service.command;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public class LogoutCommand {
	private final String accessToken;
	private final String refreshToken;
	private final String ip;
}