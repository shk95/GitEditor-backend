package com.shk95.giteditor.domain.application.impl;

import com.shk95.giteditor.domain.application.GithubService;
import com.shk95.giteditor.domain.model.github.GithubInitializer;
import com.shk95.giteditor.domain.model.github.GithubUserCredentialDelegator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHTree;
import org.kohsuke.github.GitHub;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class GithubServiceImpl implements GithubService {

	private final GithubInitializer initializer;

	@Override
	public Map<String, GHRepository> getRepos(GithubUserCredentialDelegator delegator) throws IOException {
		GitHub github = initializer.getInstance(delegator);
		String userName = delegator.getUserId();
		return github.getUser(userName).getRepositories();
	}

	@Override
	public GHTree getTreeRecursively(GithubUserCredentialDelegator delegator, String repositoryName) throws IOException {
		GitHub gitHub = initializer.getInstance(delegator);
		String userName = delegator.getUserId();

		GHRepository repository = gitHub.getUser(userName).getRepository(repositoryName);
		GHCommit commit = repository.getCommit(repository.getBranches().get(repository.getDefaultBranch()).getSHA1());

		return commit.getTree();
	}
}
