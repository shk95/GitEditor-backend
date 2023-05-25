package com.shk95.giteditor.domain.application.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class GetReposCommand {

	private final String username;

	public boolean isMine() {
		return this.username == null;
	}
}
