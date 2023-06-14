package com.shk95.giteditor.domain.application.commands.github;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReadFileCommand {

	private String owner;
	private String branchName;

	private String repoName;
	private String sha;


	public boolean isMine() {
		return this.owner == null;
	}
}
