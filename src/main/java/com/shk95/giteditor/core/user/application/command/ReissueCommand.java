package com.shk95.giteditor.core.user.application.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReissueCommand {

	private String accessToken;
	private String refreshToken;

	private ReissueCommand() {
	}
}
