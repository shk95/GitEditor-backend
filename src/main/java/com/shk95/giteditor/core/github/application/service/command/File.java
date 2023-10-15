package com.shk95.giteditor.core.github.application.service.command;

import com.shk95.giteditor.core.github.domain.GithubFileMode;
import lombok.Builder;
import lombok.Getter;

@Getter
public abstract class File {

	protected String repoName;
	protected String branchName;
	protected String path;

	protected File(String repoName, String branchName, String path) {
		this.repoName = repoName;
		this.branchName = branchName;
		this.path = path;
	}

	public static CreateCommand.CreateCommandBuilder forCreate() {
		return CreateCommand.builder();
	}

	public static ReadCommand.ReadCommandBuilder forRead() {
		return ReadCommand.builder();
	}

	public static UpdateCommand.UpdateCommandBuilder forUpdate() {
		return UpdateCommand.builder();
	}

	public static DeleteCommand.DeleteCommandBuilder forDelete() {
		return DeleteCommand.builder();
	}

	@Getter
	public static class CreateCommand extends File {

		private final String baseTreeSha;
		private final GithubFileMode mode;
		private final String commitMessage;
		private final String content;

		@Builder
		private CreateCommand(String repoName, String branchName, String path, String content, GithubFileMode mode, String commitMessage, String baseTreeSha) {
			super(repoName, branchName, path);
			this.baseTreeSha = baseTreeSha;
			this.mode = mode;
			this.commitMessage = commitMessage;
			this.content = content;
		}

		public boolean isExecutable() {
			return this.mode.isExecutable();
		}
	}

	@Getter
	public static class ReadCommand extends File {

		private final String owner;

		private final String sha;

		@Builder
		private ReadCommand(String repoName, String branchName, String path, String owner, String sha) {
			super(repoName, branchName, path);
			this.owner = owner;
			this.sha = sha;
		}

		public boolean isMine() {
			return this.owner == null;
		}
	}

	@Getter
	public static class UpdateCommand extends File {

		private final String commitMessage;
		private final String content;

		@Builder
		private UpdateCommand(String repoName, String branchName, String path, String commitMessage, String content) {
			super(repoName, branchName, path);
			this.commitMessage = commitMessage;
			this.content = content;
		}
	}

	@Getter
	public static class DeleteCommand extends File {

		private final String baseTreeSha;
		private final String fileSha;
		private final String commitMessage;

		@Builder
		private DeleteCommand(String repoName, String branchName, String path, String baseTreeSha, String fileSha, String commitMessage) {
			super(repoName, branchName, path);
			this.baseTreeSha = baseTreeSha;
			this.fileSha = fileSha;
			this.commitMessage = commitMessage;
		}
	}

}
