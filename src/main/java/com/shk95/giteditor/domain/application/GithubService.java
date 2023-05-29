package com.shk95.giteditor.domain.application;

import com.shk95.giteditor.domain.model.github.GithubUserCredentialDelegator;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTree;

import java.io.IOException;
import java.util.Map;

public interface GithubService {
	Map<String, GHRepository> getRepos(GithubUserCredentialDelegator delegator) throws IOException;

	GHTree getTreeRecursively(GithubUserCredentialDelegator delegator, String repositoryName) throws IOException;
}
