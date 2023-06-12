package com.shk95.giteditor.domain.application.commands.github;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateBranchCommand {

	private String newBranchName;
	private String baseBranchName;
	private String repoName;
}
