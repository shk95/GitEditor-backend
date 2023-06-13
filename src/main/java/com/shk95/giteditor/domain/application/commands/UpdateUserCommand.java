package com.shk95.giteditor.domain.application.commands;

import com.shk95.giteditor.domain.model.user.UserId;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateUserCommand {

	private UserId userId;
	private String email;
	private String username;
	private String password;

	public boolean isEmail() {
		return email != null;
	}

	public boolean isUsername() {
		return username != null;
	}

	public boolean isPassword() {
		return password != null;
	}
}
