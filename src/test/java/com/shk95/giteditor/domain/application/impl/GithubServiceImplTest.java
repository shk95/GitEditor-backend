package com.shk95.giteditor.domain.application.impl;

import com.shk95.giteditor.domain.application.GithubService;
import com.shk95.giteditor.domain.application.commands.github.GetFilesCommand;
import com.shk95.giteditor.domain.application.commands.github.GetReposCommand;
import com.shk95.giteditor.domain.common.constant.ProviderType;
import com.shk95.giteditor.domain.model.github.*;
import com.shk95.giteditor.domain.model.provider.Provider;
import com.shk95.giteditor.domain.model.provider.ProviderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GitHubBuilder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.isA;

@WithMockUser
@DisplayName("Production Test for GitService")
@Transactional(readOnly = true)
@ActiveProfiles("prod")
@SpringBootTest
class GithubServiceImplTest {

	@Autowired
	GithubService githubService;
	@Autowired
	ProviderRepository providerRepository;


	@MockBean
	GithubInitializer initializer;

	private ServiceUserInfo userInfo;

	@BeforeEach
	public void provideCredential() throws IOException {
		Provider provider = providerRepository.findAll().stream().filter(
			p -> p.getProviderId().getProviderType() == ProviderType.GITHUB
		).findFirst().orElseThrow(RuntimeException::new);

		this.userInfo = new ServiceUserInfo(provider.getProviderLoginId(), ProviderType.GITHUB);

		GithubCredentialDelegator credentialDelegator = new GithubCredentialDelegator(provider.getAccessToken(), provider.getProviderLoginId());

		Mockito.when(initializer.getInstance(isA(GithubCredentialDelegator.class))).thenReturn(
			new GitHubBuilder().withOAuthToken(credentialDelegator.getAccessToken(), credentialDelegator.getGithubLoginId()).build());
	}

	@Test
	void getRepos() throws IOException {
		GetReposCommand command = GetReposCommand.builder().build();
		githubService.getRepos(userInfo, command).forEach(
			(g) -> System.out.println("repo full name : " + g.getRepoFullName())
		);
	}

	@Test
	void getTreeRecursively() throws IOException {
		GetReposCommand command = GetReposCommand.builder().build();
		String someRepo = githubService.getRepos(userInfo, command).get(0).getRepoName();
		githubService.getFiles(userInfo, GetFilesCommand.builder()
			.repositoryName(someRepo).recursive(true).build()).forEach(t -> {
			System.out.println(t.getMode());
			System.out.println(t.getPath());
			System.out.println(t.getType());
			System.out.println(t.getSha());
			System.out.println(t.getUrl());
			System.out.println();
		});
	}

	@Test
	void getTreeFilesFromRoot() throws IOException {
		GetReposCommand command = GetReposCommand.builder().build();
		String someRepo = githubService.getRepos(userInfo, command).get(0).getRepoName();

		List<GithubFile> f = githubService.getFiles(userInfo, GetFilesCommand.builder().repositoryName(someRepo).build());
		f.forEach(
			t -> {
				System.out.println(t.getType().getCode());
				System.out.println(t.getMode().getCode());
				System.out.println(t.getSha());
				System.out.println(t.getPath());
				System.out.println(t.getUrl());
				System.out.println();
			}
		);
		System.out.println("size : " + f.size());
	}

	@Test
	void getFilesByTreeSha() throws IOException {
		GetReposCommand command = GetReposCommand.builder().build();
		String someRepo = githubService.getRepos(userInfo, command).get(0).getRepoName();
		GithubFile ifFileTypeTree = githubService
			.getFiles(userInfo, GetFilesCommand.builder().repositoryName(someRepo).build())
			.stream()
			.filter(githubFile -> githubFile.getType() == GithubFileType.TREE).findAny().orElseThrow(IOException::new);

		githubService.getFiles(userInfo, GetFilesCommand.builder()
			.repositoryName(someRepo)
			.treeSha(ifFileTypeTree.getSha()).build()).parallelStream().forEachOrdered(
			t -> {
				System.out.println(t.getType().getCode());
				System.out.println(t.getMode().getCode());
				System.out.println(t.getSha());
				System.out.println(t.getPath());
				System.out.println(t.getUrl());
				System.out.println();
			}
		);
	}
}
