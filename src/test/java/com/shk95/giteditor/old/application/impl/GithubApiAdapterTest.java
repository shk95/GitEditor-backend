package com.shk95.giteditor.old.application.impl;

import com.shk95.giteditor.common.ServiceUserId;
import com.shk95.giteditor.common.constant.ProviderType;
import com.shk95.giteditor.core.github.application.port.in.GithubServiceUseCase;
import com.shk95.giteditor.core.github.application.service.command.GetFilesCommand;
import com.shk95.giteditor.core.github.application.service.command.GetReposCommand;
import com.shk95.giteditor.core.github.domain.GithubCredential;
import com.shk95.giteditor.core.github.domain.GithubFile;
import com.shk95.giteditor.core.github.domain.GithubFileType;
import com.shk95.giteditor.core.github.infrastructure.GithubInitializer;
import com.shk95.giteditor.core.user.domain.provider.Provider;
import com.shk95.giteditor.core.user.domain.provider.ProviderRepository;
import com.shk95.giteditor.core.user.domain.user.UserId;
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
class GithubApiAdapterTest {

	@Autowired
	GithubServiceUseCase githubServiceUseCase;
	@Autowired
	ProviderRepository providerRepository;

	@MockBean
	GithubInitializer initializer;

	private ServiceUserId serviceUserId;

	@BeforeEach
	public void provideCredential() throws IOException {
		Provider provider = providerRepository.findAll().stream().filter(
			p -> p.getProviderId().getProviderType() == ProviderType.GITHUB
		).findFirst().orElseThrow(RuntimeException::new);

		this.serviceUserId = ServiceUserId.from(new UserId(ProviderType.GITHUB, provider.getProviderLoginId()));

		GithubCredential credentialDelegator = new GithubCredential(provider.getAccessToken(), provider.getProviderLoginId());

		Mockito.when(initializer.getInstance(isA(GithubCredential.class))).thenReturn(
			new GitHubBuilder().withOAuthToken(credentialDelegator.getAccessToken(), credentialDelegator.getGithubLoginId()).build());
	}

	@Test
	void getRepos() throws IOException {
		GetReposCommand command = GetReposCommand.builder().build();
		githubServiceUseCase.getRepos(serviceUserId.get(), command).forEach(
			(g) -> System.out.println("repo full name : " + g.getRepoFullName())
		);
	}

	@Test
	void getTreeRecursively() throws IOException {
		GetReposCommand command = GetReposCommand.builder().build();
		String someRepo = githubServiceUseCase.getRepos(serviceUserId.get(), command).get(0).getRepoName();
		githubServiceUseCase.getFiles(serviceUserId.get() , GetFilesCommand.builder()
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
		String someRepo = githubServiceUseCase.getRepos(serviceUserId.get(), command).get(0).getRepoName();

		List<GithubFile> f = githubServiceUseCase.getFiles(serviceUserId.get(), GetFilesCommand.builder().repositoryName(someRepo).build());
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
		String someRepo = githubServiceUseCase.getRepos(serviceUserId.get(), command).get(0).getRepoName();
		GithubFile ifFileTypeTree = githubServiceUseCase
			.getFiles(serviceUserId.get(), GetFilesCommand.builder().repositoryName(someRepo).build())
			.stream()
			.filter(githubFile -> githubFile.getType() == GithubFileType.TREE).findAny().orElseThrow(IOException::new);

		githubServiceUseCase.getFiles(serviceUserId.get(), GetFilesCommand.builder()
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
