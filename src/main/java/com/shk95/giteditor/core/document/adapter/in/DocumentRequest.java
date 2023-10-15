package com.shk95.giteditor.core.document.adapter.in;

import lombok.Getter;

public class DocumentRequest {

	public static class Github {
		@Getter
		public static class Content {
			private String fileSha;
			private String repoName;
			private String branchName;
			private String path;
			private String filename;
			private String content;
		}

		@Getter
		public static class Read {
			private String repoName;
			private String branchName;
			private String path;

			private String owner;
		}

		@Getter
		public static class Delete {
			private String mode;
			private String branchName;
			private String path;
			private String repoName;
			private String fileSha;
		}
	}

	public static class Markdown {

		@Getter
		public static class Create {

			private String url;

			private String branchName;
			private String baseTreeSha;
			private String basePath;

			private String storageType;
			private String repoName;
			private String path;
			private String filename;
		}
	}
}
