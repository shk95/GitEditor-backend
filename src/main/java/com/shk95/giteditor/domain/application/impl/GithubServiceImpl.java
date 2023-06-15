package com.shk95.giteditor.domain.application.impl;

import com.shk95.giteditor.domain.application.GithubService;
import com.shk95.giteditor.domain.application.commands.github.*;
import com.shk95.giteditor.domain.model.github.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.*;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class GithubServiceImpl implements GithubService {

	private final GithubInitializer initializer;
	private final GithubCredentialResolver credentialResolver;

	private static String readBlobAsString(GHBlob content) {
		StringBuilder sb = new StringBuilder();
		try {
			InputStream is = content.read();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			log.warn("Unexpected exception occurred while reading blob as string. {}", e.getMessage());
			return null;
		}
		return sb.toString();
	}

	@Override
	public List<GithubRepo> getRepos(ServiceUserInfo userInfo, GetReposCommand command) throws IOException {
		GitHub github = initializer.getInstance(credentialResolver.getCredential(userInfo.getUserId()));

		Map<String, GHRepository> repositories = command.isMine()
			? github.getMyself().getAllRepositories()
			: github.getUser(command.getOwner()).getRepositories();

		List<GithubRepo> repos = new ArrayList<>();
		repositories.forEach((repoName, getRepo) -> {
			repoBuilder(repoName, getRepo);
			repos.add(repoBuilder(repoName, getRepo));
		});
		return repos;
	}

	@Override
	public GithubRepo getRepoInfo(ServiceUserInfo userInfo, String repoName) throws IOException {
		GitHub github = initializer.getInstance(credentialResolver.getCredential(userInfo.getUserId()));
		GHRepository repository = github.getMyself().getRepository(repoName);
		return repoBuilder(repoName, repository);
	}

	@Override
	public List<GithubFile> getFiles(ServiceUserInfo userInfo, GetFilesCommand command) throws IOException {
		GitHub github = initializer.getInstance(credentialResolver.getCredential(userInfo.getUserId()));

		String userName = command.getOwner();// 자신의 repository 조회시 null

		GHRepository repository = command.isMine()
			? github.getMyself().getRepository(command.getRepositoryName())
			: github.getUser(userName).getRepository(command.getRepositoryName());

		String treeSha = command.fromRoot()
			? repository.getBranches().get(command.isDefaultBranch() ? repository.getDefaultBranch() : command.getBranchName()).getSHA1()
			: command.getTreeSha();
		GHTree tree = command.isRecursive()
			? repository.getTreeRecursive(treeSha, 1)
			: repository.getTree(treeSha);

		List<GithubFile> files = new ArrayList<>();
		tree.getTree().forEach(
			t -> {
				GithubFile githubFile = GithubFile.builder()
					.sha(t.getSha())
					.url(t.getUrl().toString())
					.path(t.getPath())
					.size(t.getSize())
					.mode(GithubFileMode.fromCode(t.getMode()))
					.type(GithubFileType.fromCode(t.getType())).build();
				files.add(githubFile);
			});
		return files;
	}

	@Override
	public void createFile(ServiceUserInfo userInfo, CreateFileCommand command) throws IOException {
		GitHub github = initializer.getInstance(credentialResolver.getCredential(userInfo.getUserId()));

		GHRepository repository = github.getMyself().getRepository(command.getRepoName());

		GHContentUpdateResponse res = repository.createContent()
			.branch(command.getBranchName())
			.message(command.getCommitMessage())
			.content(command.getContent())
			.path(command.getBasePath() + "/" + command.getFilename())
			.commit();
	}

	@Override
	public GithubFile readBlobAsString(ServiceUserInfo userInfo, ReadFileCommand command) throws IOException {
		GitHub github = initializer.getInstance(credentialResolver.getCredential(userInfo.getUserId()));

		GHRepository repository = command.isMine()
			? github.getMyself().getRepository(command.getRepoName())
			: github.getUser(command.getOwner()).getRepository(command.getRepoName());

		GHBlob content = repository.getBlob(command.getSha());

		String textContent = readBlobAsString(content);

		return GithubFile.builder()
			.textContent(textContent)
			.sha(content.getSha())
			.url(content.getUrl().toString())
			.build();
	}

	@Override
	public boolean commitFiles(ServiceUserInfo userInfo, CommitFilesCommand command) throws IOException {
		GitHub github = initializer.getInstance(credentialResolver.getCredential(userInfo.getUserId()));

		GHRepository repository = command.isMine()
			? github.getMyself().getRepository(command.getRepositoryName())
			: github.getUser(command.getOwner()).getRepository(command.getRepositoryName());

		List<GithubFile> createdFiles = new ArrayList<>();
		command.getFiles().forEach
			(file -> {
				try {
					GHTree tree = repository.createTree().add(
							file.getPath(), file.getBlobContent().getByteContent(), file.getMode().isExecutable())
						.baseTree(command.getBaseTreeSha()).create();
					createdFiles.add(GithubFile.builder()
						.sha(tree.getSha()).build());

				} catch (IOException e) {
					log.info("{}.commitFiles | some errors occurred while creating tree. {}", getClass().getName(), e.getMessage());
				}
			});

		/*try {
			repository.createCommit()
				.message(command.getCommitMessage())
				.tree(command.getTreeSha())

				.parent(command.getParentSha())
				.create();
		} catch (IOException e) {
			log.info("{}.commitFiles | some errors occurred while creating commit. {}", getClass().getName(), e.getMessage());
		}*/


		return false;
	}

	@Override
	public void createBranch(ServiceUserInfo userInfo, CreateBranchCommand command) throws IOException {
		GitHub github = initializer.getInstance(credentialResolver.getCredential(userInfo.getUserId()));

		String refBranchSha = github.getMyself().getRepository(command.getRepoName()).getBranch(command.getBaseBranchName()).getSHA1();
		GHRef newBranch = github.getMyself().getRepository(command.getRepoName())
			.createRef("refs/heads/" + command.getNewBranchName(), refBranchSha);
	}

	@Override
	public void deleteBranch(ServiceUserInfo userInfo, String branchName) throws IOException {
		GitHub github = initializer.getInstance(credentialResolver.getCredential(userInfo.getUserId()));

	}

	@Override
	public String createRepo(ServiceUserInfo userInfo, CreateRepoCommand command) throws IOException {
		GitHub github = initializer.getInstance(credentialResolver.getCredential(userInfo.getUserId()));
		return github.createRepository(command.getRepoName())
			.description(command.getDescription())
			.private_(command.isMakePrivate()).create()
			.getName();
	}

	@Override
	public void deleteRepo(ServiceUserInfo userInfo, String repoName) throws IOException {
		GitHub github = initializer.getInstance(credentialResolver.getCredential(userInfo.getUserId()));

	}

	@Override
	public GithubFile readBlobAsString(ServiceUserInfo userInfo, GetFilesCommand command) throws IOException {
		GitHub github = initializer.getInstance(credentialResolver.getCredential(userInfo.getUserId()));

		return null;
	}

	private GithubRepo repoBuilder(String repoName, GHRepository repository) {
		GithubRepo.GithubRepoBuilder repo = GithubRepo.builder()
			.repoName(repoName)
			.repoFullName(repository.getFullName())
			.htmlUrl(repository.getHtmlUrl().toString())
			.defaultBranch(repository.getDefaultBranch())
			.url(repository.getUrl().toString());

		List<GithubBranch> branches = new ArrayList<>();
		try {
			repository.getBranches()
				.forEach((k, v) -> branches.add(GithubBranch.builder().branchName(k).branchSha(v.getSHA1()).build()));
		} catch (IOException e) {
			log.debug("{}.getRepos | some errors occurred while getting branches. {}", getClass().getName(), e.getMessage());
		}
		repo.branches(branches);

		GithubOwner owner = null;
		try {
			GHUser user = repository.getOwner();
			owner = GithubOwner.builder()
				.email(user.getEmail()).name(user.getName())
				.loginId(user.getLogin()).avatarUrl(user.getAvatarUrl())
				.type(user.getType()).build();
		} catch (IOException e) {
			log.debug("{}.getRepos | some errors occurred while getting the owner. {}", getClass().getName(), e.getMessage());
		}
		repo.owner(owner);
		return repo.build();
	}
}
