package com.shk95.giteditor.domain.application.commands.github;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateFileCommand {

	private String path;
	private String repoName;
	private String branchName;
	private String commitMessage;
	private String content;
}
