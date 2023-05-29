package com.shk95.giteditor.domain.application.commands;

import com.shk95.giteditor.web.apis.request.AuthRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginCommand {
	private final String userId;
	private final String password;
	private final String ip;

	public static LoginCommand of(AuthRequest.Login login, String ip) {
		return LoginCommand.builder().userId(login.getUserId()).password(login.getPassword()).ip(ip).build();
	}
}
