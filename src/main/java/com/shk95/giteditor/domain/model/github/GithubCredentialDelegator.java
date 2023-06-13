package com.shk95.giteditor.domain.model.github;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GithubCredentialDelegator {
	private String accessToken;
	private String githubLoginId;

	GithubCredentialDelegator() {
	}

	public boolean isInstantiate() {
		return accessToken != null && githubLoginId != null;
	}
}
