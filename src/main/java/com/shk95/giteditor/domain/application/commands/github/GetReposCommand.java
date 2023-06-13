package com.shk95.giteditor.domain.application.commands.github;

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
