package com.shk95.giteditor.domain.application.commands;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResolverCommand {
	private String accessToken;
	private String refreshToken;
}
