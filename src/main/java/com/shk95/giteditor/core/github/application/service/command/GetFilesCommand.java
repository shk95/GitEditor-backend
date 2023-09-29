package com.shk95.giteditor.core.github.application.service.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetFilesCommand {

	private final String owner;
	private final String branchName;
	private final String treeSha;
	private final boolean recursive;

	private final String fileSha;
	private final String repositoryName;
	private final String path;

	/**
	 * Search with default branch if branchName is null.
	 *
	 * @return If the condition is true, it will be considered as the default branch.
	 */
	public boolean isDefaultBranch() {
		return this.branchName != null;
	}

	public boolean isMine() {
		return this.owner == null;
	}

	public boolean isRecursive() {
		return this.recursive;
	}

	public boolean fromRoot() {
		return this.treeSha == null;
	}
}
