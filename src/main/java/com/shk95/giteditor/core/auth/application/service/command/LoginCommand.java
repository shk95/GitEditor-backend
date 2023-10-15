package com.shk95.giteditor.core.auth.application.service.command;

import com.shk95.giteditor.core.auth.adapter.in.AuthRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginCommand {
	private final String userId;
	private final String password;

	public static LoginCommand of(AuthRequest.Login login) {
		return LoginCommand.builder().userId(login.getUserId()).password(login.getPassword()).build();
	}
}
