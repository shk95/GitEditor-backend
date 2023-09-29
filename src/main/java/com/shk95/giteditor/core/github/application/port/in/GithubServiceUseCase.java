package com.shk95.giteditor.core.github.application.port.in;

import com.shk95.giteditor.core.github.application.service.command.*;
import com.shk95.giteditor.core.github.domain.GithubFile;
import com.shk95.giteditor.core.github.domain.GithubRepo;

import java.io.IOException;
import java.util.List;

public interface GithubServiceUseCase {

	List<GithubRepo> getRepos(String userId, GetReposCommand command) throws IOException;

	GithubRepo getRepo(String userId, String repoName) throws IOException;

	List<GithubFile> getFiles(String userId, GetFilesCommand command) throws IOException;

	void createFile(String userId, File.CreateCommand command) throws IOException;

	GithubFile readBlobAsString(String userId, File.ReadCommand command) throws IOException;

	void createBranch(String userId, Branch.CreateCommand command) throws IOException;

	void deleteBranch(String userId, Branch.DeleteCommand command) throws IOException;

	String createRepo(String userId, Repo.CreateCommand command) throws IOException;

	void deleteRepo(String userId, Repo.DeleteCommand command) throws IOException;

	void deleteFile(String userId, File.DeleteCommand command) throws IOException;

	void updateFile(String userId, File.UpdateCommand command) throws IOException;

	boolean commitFiles(String userId, CommitFilesCommand command) throws IOException;

	void deleteDirectory(String userId, File.DeleteCommand command) throws IOException;

	GithubFile readBlobAsString(String userId, GetFilesCommand command) throws IOException;
}
