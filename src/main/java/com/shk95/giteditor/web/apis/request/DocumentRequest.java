package com.shk95.giteditor.web.apis.request;

import lombok.Getter;

public class DocumentRequest {

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
