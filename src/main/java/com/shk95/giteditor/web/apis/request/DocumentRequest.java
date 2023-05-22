package com.shk95.giteditor.web.apis.request;

import lombok.Getter;

public class DocumentRequest {

	public static class Markdown {

		@Getter
		public static class Create {

			private String url;
			private String storageType;
			private String path;
			private String repoName;
			private String fileName;
		}
	}
}
