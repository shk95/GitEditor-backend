package com.shk95.giteditor.domain.model.github;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GithubInitializer {

	public GitHub getInstance(GithubUserCredentialDelegator delegator) throws IOException {
		return new GitHubBuilder().withOAuthToken(delegator.getAccessToken(), delegator.getUserId()).build();
	}
}
