package com.shk95.giteditor.web.apis.request;

import lombok.Getter;

public class GithubRequest {

	@Getter
	public static class CreateBranch {

		private String newBranchName;
		private String baseBranchName;
		private String baseBranchSha;
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
			private String path;
			private String content;// base64 encoded
			private String mode;

			private String commitMessage;
			private String baseTreeSha;
		}
	}
}
