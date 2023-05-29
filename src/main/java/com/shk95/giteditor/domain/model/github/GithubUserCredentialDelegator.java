package com.shk95.giteditor.domain.model.github;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GithubUserCredentialDelegator {
	private String accessToken;
	private String userId;

	private GithubUserCredentialDelegator() {
	}
}
