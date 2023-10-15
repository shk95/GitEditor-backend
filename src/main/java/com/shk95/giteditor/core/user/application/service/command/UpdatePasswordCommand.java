package com.shk95.giteditor.core.user.application.service.command;

import com.shk95.giteditor.core.user.domain.user.UserId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdatePasswordCommand {
	private UserId userId;
	private String inputPassword;
	private String defaultEmail;

	public UpdatePasswordCommand(UserId userId, String inputPassword) {
		this.userId = userId;
		this.inputPassword = inputPassword;
	}

	public UpdatePasswordCommand(String defaultEmail) {
		this.defaultEmail = defaultEmail;
	}

	public boolean isPasswordForgot() {
		return this.defaultEmail != null;
	}
}
