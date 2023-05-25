package com.shk95.giteditor.domain.application.commands;

import com.shk95.giteditor.domain.model.user.UserId;
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

	public UpdatePasswordCommand(String defaultEmail, String inputPassword) {
		this.defaultEmail = defaultEmail;
		this.inputPassword = inputPassword;
	}

	public boolean isPasswordForgot() {
		return this.defaultEmail != null;
	}
}
