package com.shk95.giteditor.domain.application.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class TokenResolverCommand {
	@Builder
	@Getter
	@AllArgsConstructor
	public static class TokenInfo {
		private String grantType;
		private String accessToken;
		private String refreshToken;
	}
}