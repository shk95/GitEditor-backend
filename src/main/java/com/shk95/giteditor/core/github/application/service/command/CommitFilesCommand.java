package com.shk95.giteditor.core.github.application.service.command;

import com.shk95.giteditor.core.github.domain.GithubFileMode;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class CommitFilesCommand {

	private final String owner;
	private final String repositoryName;
	private final String branchName;
	private final String commitMessage;
	private final String baseTreeSha;

	private final List<File> files;

	public boolean isMine() {
		return this.owner == null;
	}

	@Getter
	@Builder
	public static class File {

		private final String path;
		private final Content blobContent;
		private GithubFileMode mode;

		@Getter
		public static class Content {

			private final byte[] byteContent;
			private String rawContent;


			public Content(String rawContent) {
				this.rawContent = rawContent;
				this.byteContent = rawContent.getBytes();
			}

			public Content(byte[] byteContent) {
				this.byteContent = byteContent;
			}
		}
	}
}
