package com.shk95.giteditor.domain.application.impl;

import com.shk95.giteditor.domain.application.GithubService;
import com.shk95.giteditor.domain.model.github.GithubUserCredentialDelegator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class GithubServiceImplTest {
	@Autowired
	GithubService githubService;

	@Test
	void getRepos() throws IOException {
		githubService.getRepos(new GithubUserCredentialDelegator("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", "shk95")).forEach(
			(d, dd) -> System.out.println(d + " " + dd.getFullName())
		);
	}

	@Test
	void getTreeRecursively() throws IOException {

	}
}
