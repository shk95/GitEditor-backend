package com.shk95.giteditor.domain.application.commands.github;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteBranchCommand {

	private String repoName;
	private String branchName;
}
