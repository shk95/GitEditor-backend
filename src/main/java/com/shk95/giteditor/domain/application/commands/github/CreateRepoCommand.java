package com.shk95.giteditor.domain.application.commands.github;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateRepoCommand {

	private String repoName;
	private boolean makePrivate;
	private String description;
}
