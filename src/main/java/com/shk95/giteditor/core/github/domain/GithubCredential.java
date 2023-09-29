package com.shk95.giteditor.core.github.domain;

import lombok.Getter;

import java.util.Objects;

@Getter
public class GithubCredential {

	private final String accessToken;
	private final String githubLoginId;

	public GithubCredential(String accessToken, String githubLoginId) {
		this.accessToken = accessToken;
		this.githubLoginId = githubLoginId;
	}

	public boolean isInstantiate() {
		return accessToken != null && githubLoginId != null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GithubCredential that = (GithubCredential) o;
		return Objects.equals(accessToken, that.accessToken) && Objects.equals(githubLoginId, that.githubLoginId);
	}

	@Override
	public String toString() {
		return "GithubCredential{" +
			"accessToken='" + accessToken + '\'' +
			", githubLoginId='" + githubLoginId + '\'' +
			'}';
	}

	@Override
	public int hashCode() {
		return Objects.hash(accessToken, githubLoginId);
	}
}
