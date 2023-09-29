package com.shk95.giteditor.core.github.application.service;

import com.shk95.giteditor.core.github.application.port.in.GithubServiceUseCase;
import com.shk95.giteditor.core.github.application.port.out.GetCredentialPort;
import com.shk95.giteditor.core.github.application.port.out.github.Operation;
import com.shk95.giteditor.core.github.application.port.out.github.Repository;
import com.shk95.giteditor.core.github.application.port.out.github.User;
import com.shk95.giteditor.core.github.application.service.command.*;
import com.shk95.giteditor.core.github.domain.GithubFile;
import com.shk95.giteditor.core.github.domain.GithubRepo;
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
	private final Repository repository;
	private final Operation operation;
	private final User user;

	@Override
	public List<GithubRepo> getRepos(String userId, GetReposCommand command) throws IOException {
		return operation.getRepos(getCredentialPort.fetch(userId), command);
	}

	@Override
	public GithubRepo getRepo(String userId, String repoName) throws IOException {
		return operation.getRepo(getCredentialPort.fetch(userId), repoName);
	}

	@Override
	public List<GithubFile> getFiles(String userId, GetFilesCommand command) throws IOException {
		return repository.getAll(getCredentialPort.fetch(userId), command);
	}

	@Override
	public void createFile(String userId, File.CreateCommand command) throws IOException {
		repository.create(getCredentialPort.fetch(userId), command);
	}

	@Override
	public GithubFile readBlobAsString(String userId, File.ReadCommand command) throws IOException {
		return repository.get(getCredentialPort.fetch(userId), command);
	}

	@Override
	public void createBranch(String userId, Branch.CreateCommand command) throws IOException {
		operation.createBranch(getCredentialPort.fetch(userId), command);
	}

	@Override
	public void deleteBranch(String userId, Branch.DeleteCommand command) throws IOException {
		operation.deleteBranch(getCredentialPort.fetch(userId), command);
	}

	@Override
	public String createRepo(String userId, Repo.CreateCommand command) throws IOException {
		return operation.createRepo(getCredentialPort.fetch(userId), command);
	}

	@Override
	public void deleteRepo(String userId, Repo.DeleteCommand command) throws IOException {
		operation.deleteRepo(getCredentialPort.fetch(userId), command);
	}

	@Override
	public void deleteFile(String userId, File.DeleteCommand command) throws IOException {
		repository.delete(getCredentialPort.fetch(userId), command);
	}

	@Override
	public void updateFile(String userId, File.UpdateCommand command) throws IOException {
		repository.update(getCredentialPort.fetch(userId), command);
	}

	@Override
	public boolean commitFiles(String userId, CommitFilesCommand command) throws IOException {
		return repository.commit(getCredentialPort.fetch(userId), command);
	}

	@Override
	public void deleteDirectory(String userId, File.DeleteCommand command) throws IOException {
		repository.deleteDirectory(getCredentialPort.fetch(userId), command);
	}

	@Override
	public GithubFile readBlobAsString(String userId, GetFilesCommand command) throws IOException {
		return repository.get(getCredentialPort.fetch(userId), command);
	}
}
