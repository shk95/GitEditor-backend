package com.shk95.giteditor.core.github.application.service.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class GetReposCommand {

	private final String owner;

	public boolean isMine() {
		return this.owner == null;
	}
}
