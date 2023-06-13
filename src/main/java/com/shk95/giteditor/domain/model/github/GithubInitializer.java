package com.shk95.giteditor.domain.model.github;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GithubInitializer {

	public GitHub getInstance(GithubCredentialDelegator delegator) throws IOException {
		return new GitHubBuilder().withOAuthToken(delegator.getAccessToken(), delegator.getGithubLoginId()).build();
	}
}
