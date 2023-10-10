package com.shk95.giteditor.core.auth.application.service.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReissueCommand {

	private String accessToken;

	private ReissueCommand() {
	}
}
