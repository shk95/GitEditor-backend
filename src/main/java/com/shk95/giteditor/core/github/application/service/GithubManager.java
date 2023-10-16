package com.shk95.giteditor.core.github.application.service;

import com.shk95.giteditor.core.github.application.port.in.GithubServiceUseCase;
import com.shk95.giteditor.core.github.application.port.out.GetCredentialPort;
import com.shk95.giteditor.core.github.application.port.out.GithubOperationPort;
import com.shk95.giteditor.core.github.application.port.out.GithubRepositoryPort;
import com.shk95.giteditor.core.github.application.service.command.*;
import com.shk95.giteditor.core.github.domain.GithubFile;
import com.shk95.giteditor.core.github.domain.GithubRepo;
import com.shk95.giteditor.core.user.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class GithubManager implements GithubServiceUseCase {

	private final GetCredentialPort getCredentialPort;
	private final GithubRepositoryPort githubRepositoryPort;
	private final GithubOperationPort githubOperationPort;

	@Override
	public List<GithubRepo> getRepos(String userId, GetReposCommand command) throws IOException {
		return githubOperationPort.getRepos(getCredentialPort.fetch(userId), command);
	}

	@Override
	public GithubRepo getRepo(String userId, String repoName) throws IOException {
		return githubOperationPort.getRepo(getCredentialPort.fetch(userId), repoName);
	}

	@Override
	public List<GithubFile> getFiles(String userId, GetFilesCommand command) throws IOException {
		return githubRepositoryPort.getAll(getCredentialPort.fetch(userId), command);
	}

	@Override
	public void createFile(String userId, File.CreateCommand command) throws IOException {
		githubRepositoryPort.create(getCredentialPort.fetch(userId), command);
	}

	@Override
	public GithubFile readBlobAsString(String userId, File.ReadCommand command) throws IOException {
		return githubRepositoryPort.get(getCredentialPort.fetch(userId), command);
	}

	@Override
	public void createBranch(String userId, Branch.CreateCommand command) throws IOException {
		githubOperationPort.createBranch(getCredentialPort.fetch(userId), command);
	}

	@Override
	public void deleteBranch(String userId, Branch.DeleteCommand command) throws IOException {
		githubOperationPort.deleteBranch(getCredentialPort.fetch(userId), command);
	}

	@Override
	public String createRepo(String userId, Repo.CreateCommand command) throws IOException {
		return githubOperationPort.createRepo(getCredentialPort.fetch(userId), command);
	}

	@Override
	public void deleteRepo(String userId, Repo.DeleteCommand command) throws IOException {
		githubOperationPort.deleteRepo(getCredentialPort.fetch(userId), command);
	}

	@Override
	public void deleteFile(String userId, File.DeleteCommand command) throws IOException {
		githubRepositoryPort.delete(getCredentialPort.fetch(userId), command);
	}

	@Override
	public void updateFile(String userId, File.UpdateCommand command) throws IOException {
		githubRepositoryPort.update(getCredentialPort.fetch(userId), command);
	}

	@Override
	public boolean commitFiles(String userId, CommitFilesCommand command) throws IOException {
		return githubRepositoryPort.commit(getCredentialPort.fetch(userId), command);
	}

	@Override
	public void deleteDirectory(String userId, File.DeleteCommand command) throws IOException {
		githubRepositoryPort.deleteDirectory(getCredentialPort.fetch(userId), command);
	}

	@Override
	public GithubFile readBlobAsString(String userId, GetFilesCommand command) throws IOException {
		return githubRepositoryPort.get(getCredentialPort.fetch(userId), command);
	}

	@Override
	public String getRepoReadme(UserId userId, String repoName) {
		try {
			return githubRepositoryPort.getRepoReadme(getCredentialPort.fetch(userId.get()), repoName);
		} catch (IOException e) {
			return "";
		}
	}
}
