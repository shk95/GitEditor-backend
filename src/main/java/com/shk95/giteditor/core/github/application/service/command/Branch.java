package com.shk95.giteditor.core.github.application.service.command;

import lombok.Getter;

@Getter
public abstract class Branch {

	protected final String repoName;
	protected final String baseBranchName;

	protected Branch(String repoName, String baseBranchName) {
		this.repoName = repoName;
		this.baseBranchName = baseBranchName;
	}

	public static CreateCommand forCreate(String repoName, String baseBranchName, String newBranchName) {
		return new CreateCommand(repoName, baseBranchName, newBranchName);
	}

	public static DeleteCommand forDelete(String repoName, String baseBranchName) {
		return new DeleteCommand(repoName, baseBranchName);
	}

	@Getter
	public static class CreateCommand extends Branch {

		private final String newBranchName;

		private CreateCommand(String repoName, String baseBranchName, String newBranchName) {
			super(repoName, baseBranchName);
			this.newBranchName = newBranchName;
		}
	}

	public static class DeleteCommand extends Branch {

		private DeleteCommand(String repoName, String baseBranchName) {
			super(repoName, baseBranchName);
		}
	}

}
