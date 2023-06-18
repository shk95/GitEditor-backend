package com.shk95.giteditor.web.apis.response;

import lombok.Builder;
import lombok.Getter;

public class GithubResponse {

	@Getter
	@Builder
	public static class Repo {

		private String repoName;
	}
}
