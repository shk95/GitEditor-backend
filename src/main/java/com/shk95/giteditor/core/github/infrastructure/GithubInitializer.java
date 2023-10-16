package com.shk95.giteditor.core.github.infrastructure;

import com.shk95.giteditor.core.github.domain.GithubCredential;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GithubInitializer {

	public GitHub getInstance(GithubCredential credential) throws IOException { // org.kohsuke.github
		return new GitHubBuilder().withOAuthToken(credential.getAccessToken(), credential.getGithubLoginId()).build();
	}
}
