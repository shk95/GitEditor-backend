package com.shk95.giteditor.core.user.application.service.command;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OAuthInfoCommand {

	Email email;

	@Setter
	@Getter
	private static class Email {
		private String userEmail;
		private boolean primary;
		private boolean verified;
	}
}
