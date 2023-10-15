package com.shk95.giteditor.core.github.adapter.in;

import lombok.Getter;

public class GithubRequest {

	@Getter
	public static class CreateBranch {

		private String newBranchName;
		private String baseBranchName;
		private String baseBranchSha;
		private String repoName;
	}

	@Getter
	public static class DeleteBranch {

		private String branchName;
		private String repoName;
	}

	public static class File {

		@Getter
		public static class Read {

		}

		@Getter
		public static class Create {

			private String repoName;
			private String branchName;
			private String path; // file path
			private String content;// base64 encoded
			private String mode;
			private String filename; // actual file s=name

			private String commitMessage;
			private String baseTreeSha;
		}
	}

	@Getter
	public static class CreateRepo {

		private String repoName;
		private boolean makePrivate;
		private String description;
	}

	@Getter
	public static class DeleteRepo {

		private String repoName;
	}
}
