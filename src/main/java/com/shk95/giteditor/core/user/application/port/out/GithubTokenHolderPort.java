package com.shk95.giteditor.core.user.application.port.out;

import com.shk95.giteditor.core.user.domain.user.GithubService;

import java.util.Optional;

public interface GithubTokenHolderPort {

	GithubService save(GithubService githubService);

	Optional<GithubService> findById(String id);

	void deleteById(String id);
}
