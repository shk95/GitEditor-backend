package com.shk95.giteditor.core.github.application.port.out;

import com.shk95.giteditor.core.github.application.service.command.Branch;
import com.shk95.giteditor.core.github.application.service.command.GetReposCommand;
import com.shk95.giteditor.core.github.application.service.command.Repo;
import com.shk95.giteditor.core.github.domain.GithubCredential;
import com.shk95.giteditor.core.github.domain.GithubRepo;

import java.io.IOException;
import java.util.List;

public interface GithubOperationPort {

	void createBranch(GithubCredential credential, Branch.CreateCommand command) throws IOException;

	void deleteBranch(GithubCredential credential, Branch.DeleteCommand command) throws IOException;

	String createRepo(GithubCredential credential, Repo.CreateCommand command) throws IOException;

	void deleteRepo(GithubCredential credential, Repo.DeleteCommand command) throws IOException;

	List<GithubRepo> getRepos(GithubCredential credential, GetReposCommand command) throws IOException;

	GithubRepo getRepo(GithubCredential credential, String repoName) throws IOException;

}
