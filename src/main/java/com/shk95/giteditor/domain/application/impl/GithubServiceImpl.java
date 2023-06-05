package com.shk95.giteditor.domain.application.impl;

import com.shk95.giteditor.domain.application.GithubService;
import com.shk95.giteditor.domain.application.commands.GetFilesCommand;
import com.shk95.giteditor.domain.application.commands.GetReposCommand;
import com.shk95.giteditor.domain.application.commands.GetTreeCommand;
import com.shk95.giteditor.domain.model.github.GHOwner;
import com.shk95.giteditor.domain.model.github.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class GithubServiceImpl implements GithubService {

	private final GHInitializer initializer;
	private final GHCredentialResolver credentialResolver;

	@Override
	public List<GHRepo> getRepos(ServiceUserInfo userInfo, GetReposCommand command) throws IOException {
		GitHub github = initializer.getInstance(credentialResolver.getCredential(userInfo.getUserId()));

		Map<String, GHRepository> repositories = command.isMine()
			? github.getMyself().getAllRepositories()
			: github.getUser(command.getUsername()).getRepositories();

		List<GHRepo> repos = new ArrayList<>();
		repositories.forEach((repoName, getRepo) -> {
			GHRepo.GHRepoBuilder repo = GHRepo.builder()
				.repoName(repoName)
				.repoFullName(getRepo.getFullName())
				.htmlUrl(getRepo.getHtmlUrl().toString())
				.defaultBranch(getRepo.getDefaultBranch())
				.url(getRepo.getUrl().toString());

			List<GHBranch> branches = new ArrayList<>();
			try {
				getRepo.getBranches()
					.forEach((k, v) -> branches.add(GHBranch.builder().branchName(k).branchSha(v.getSHA1()).build()));
			} catch (IOException e) {
				log.debug("{}.getRepos | some errors occurred while getting branches", getClass().getName());
				throw new RuntimeException(e);
			}
			repo.branches(branches);

			GHOwner owner;
			try {
				GHUser user = getRepo.getOwner();
				owner = GHOwner.builder()
					.email(user.getEmail()).name(user.getName())
					.loginId(user.getLogin()).avatarUrl(user.getAvatarUrl())
					.type(user.getType()).build();
			} catch (IOException e) {
				log.debug("{}.getRepos | some errors occurred while getting the owner", getClass().getName());
				throw new RuntimeException(e);
			}
			repo.owner(owner);

			repos.add(repo.build());
		});
		return repos;
	}

	@Override
	public GHRepo getRepo(ServiceUserInfo userInfo) throws IOException {
		return null;
	}

	@Override
	public List<GHFile> getFilesRecursively(ServiceUserInfo userInfo, GetTreeCommand command) throws IOException {
		GitHub github = initializer.getInstance(credentialResolver.getCredential(userInfo.getUserId()));
		String userName = userInfo.getServiceUserId();

		List<GHFile> files = new ArrayList<>();

		GHRepository repository = github.getUser(userName).getRepository(command.getRepositoryName());
		repository.getTreeRecursive(repository.getBranches()
				.get(command.isBranch() ? command.getBranch() : repository.getDefaultBranch()).getSHA1(), 1)
			.getTree().forEach(
				t -> {
					GHFile ghFile = GHFile.builder()
						.sha(t.getSha())
						.url(t.getUrl().toString())
						.path(t.getPath())
						.mode(GHFileMode.fromCode(t.getMode()))
						.type(GHFileType.fromCode(t.getType())).build();
					files.add(ghFile);
				}
			);
		return files;
	}

	@Override
	public List<GHFile> getFilesFromRoot(ServiceUserInfo userInfo, GetFilesCommand command) throws IOException {
		GitHub github = initializer.getInstance(credentialResolver.getCredential(userInfo.getUserId()));
		String userName = command.getUsername();// 자신의 repository 조회시 null

		GHRepository repository = command.isMine()
			? github.getMyself().getRepository(command.getRepositoryName())
			: github.getUser(userName).getRepository(command.getRepositoryName());

		List<GHFile> files = new ArrayList<>();

		repository.getTree(repository.getBranches().get(
				command.isBranch() ? command.getBranchName() : repository.getDefaultBranch()).getSHA1()).getTree()
			.forEach(
				t -> {
					GHFile ghFile = GHFile.builder()
						.sha(t.getSha())
						.url(t.getUrl().toString())
						.path(t.getPath())
						.mode(GHFileMode.fromCode(t.getMode()))
						.type(GHFileType.fromCode(t.getType())).build();
					files.add(ghFile);
				}
			);
		return files;
	}

	@Override
	public List<GHFile> getFilesByTreeSha(ServiceUserInfo userInfo, GetFilesCommand command) throws IOException {
		GitHub github = initializer.getInstance(credentialResolver.getCredential(userInfo.getUserId()));

		GHRepository repository = command.isMine()
			? github.getMyself().getRepository(command.getRepositoryName())
			: github.getUser(command.getUsername()).getRepository(command.getRepositoryName());

		List<GHFile> files = new ArrayList<>();
		repository.getTree(command.getTreeSha()).getTree().parallelStream().forEachOrdered(
			t -> {
				GHFile ghFile = GHFile.builder()
					.sha(t.getSha())
					.url(t.getUrl().toString())
					.path(t.getPath())
					.mode(GHFileMode.fromCode(t.getMode()))
					.type(GHFileType.fromCode(t.getType())).build();
				files.add(ghFile);
			}
		);
		return files;
	}

	public void addFilesAndCommit(GHCredentialDelegator delegator, String... string) throws IOException {
		GitHub gitHub = initializer.getInstance(delegator);
	}

}
