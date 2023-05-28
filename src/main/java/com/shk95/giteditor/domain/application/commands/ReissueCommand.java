package com.shk95.giteditor.domain.application.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReissueCommand {

	private String accessToken;
	private String refreshToken;
	private String ip;

	private ReissueCommand() {
	}
}
