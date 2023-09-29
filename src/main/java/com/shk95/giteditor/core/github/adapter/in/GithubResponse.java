package com.shk95.giteditor.core.github.adapter.in;

import lombok.Builder;
import lombok.Getter;

public class GithubResponse {

	@Getter
	@Builder
	public static class Repo {

		private String repoName;
	}
}
