package com.shk95.giteditor.domain.application.commands;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetFilesCommand {
	private final String treeSha;
	private final String repositoryName;
	private final String branchName;
	private final String username;

	public boolean isBranch() {
		return this.branchName != null;
	}

	public boolean isMine() {
		return this.username == null;
	}
}
