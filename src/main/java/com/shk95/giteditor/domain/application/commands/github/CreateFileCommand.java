package com.shk95.giteditor.domain.application.commands.github;

import com.shk95.giteditor.domain.model.github.GithubFileMode;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateFileCommand {

	private final String repoName;
	private final String branchName;
	private final String path;
	private final String content;// base64 encoded
	private final GithubFileMode mode;

	private final String commitMessage;
	private final String baseTreeSha;

	public boolean isExecutable() {
		return mode.isExecutable();
	}
}
