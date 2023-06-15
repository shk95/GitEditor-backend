package com.shk95.giteditor.domain.application;

import com.shk95.giteditor.domain.application.commands.github.*;
import com.shk95.giteditor.domain.model.github.GithubFile;
import com.shk95.giteditor.domain.model.github.GithubRepo;
import com.shk95.giteditor.domain.model.github.ServiceUserInfo;

import java.io.IOException;
import java.util.List;

public interface GithubService {

	List<GithubRepo> getRepos(ServiceUserInfo userInfo, GetReposCommand command) throws IOException;

	GithubRepo getRepoInfo(ServiceUserInfo userInfo, String repoName) throws IOException;

	List<GithubFile> getFiles(ServiceUserInfo userInfo, GetFilesCommand command) throws IOException;

	void createFile(ServiceUserInfo userInfo, CreateFileCommand command) throws IOException;

	GithubFile readBlobAsString(ServiceUserInfo userInfo, ReadFileCommand command) throws IOException;

	boolean commitFiles(ServiceUserInfo userInfo, CommitFilesCommand command) throws IOException;

	void createBranch(ServiceUserInfo userInfo, CreateBranchCommand command) throws IOException;

	void deleteBranch(ServiceUserInfo userInfo, String branchName) throws IOException;

	String createRepo(ServiceUserInfo userInfo, CreateRepoCommand command) throws IOException;

	void deleteRepo(ServiceUserInfo userInfo, String repoName) throws IOException;

	GithubFile readBlobAsString(ServiceUserInfo userInfo, GetFilesCommand command) throws IOException;
}
