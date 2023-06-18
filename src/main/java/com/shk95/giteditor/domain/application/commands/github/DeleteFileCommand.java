package com.shk95.giteditor.domain.application.commands.github;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteFileCommand {

	private String branchName;
	private String baseTreeSha;
	private String path;
	private String repoName;
	private String fileSha;
	private String commitMessage;
}
