package com.shk95.giteditor.core.user.application.service.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class SignupCommand {
	@AllArgsConstructor
	@Builder
	@Getter
	public static class Default {
		private String userId;

		private String defaultEmail;

		private String password;

		private String username;

		private Default() {
		}
	}

	@AllArgsConstructor
	@Builder
	@Getter
	public static class OAuth {
		private String userId;

		private String username;

		private OAuth() {
		}
	}
}
