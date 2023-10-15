package com.shk95.giteditor.core.github.application.service.command;

import lombok.Getter;

@Getter
public abstract class Repo {

	protected final String repoName;

	protected Repo(String repoName) {
		this.repoName = repoName;
	}

	public static CreateCommand forCreate(String repoName, String description, boolean makePrivate) {
		return new CreateCommand(repoName, description, makePrivate);
	}

	public static DeleteCommand forDelete(String repoName) {
		return new DeleteCommand(repoName);
	}

	@Getter
	public static class CreateCommand extends Repo {

		private final String description;
		private final boolean makePrivate;

		private CreateCommand(String repoName, String description, boolean makePrivate) {
			super(repoName);
			this.description = description;
			this.makePrivate = makePrivate;
		}
	}

	@Getter
	public static class DeleteCommand extends Repo {

		private DeleteCommand(String repoName) {
			super(repoName);
		}
	}
}
