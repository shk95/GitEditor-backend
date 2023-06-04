package com.shk95.giteditor.domain.application.commands;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetTreeCommand {
	private String repositoryName;
	private String branch;

	public boolean isBranch() {
		return branch != null;
	}
}
