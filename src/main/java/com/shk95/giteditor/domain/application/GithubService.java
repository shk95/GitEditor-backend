package com.shk95.giteditor.domain.application;

import com.shk95.giteditor.domain.application.commands.GetFilesCommand;
import com.shk95.giteditor.domain.application.commands.GetReposCommand;
import com.shk95.giteditor.domain.application.commands.GetTreeCommand;
import com.shk95.giteditor.domain.model.github.GHFile;
import com.shk95.giteditor.domain.model.github.GHRepo;
import com.shk95.giteditor.domain.model.github.ServiceUserInfo;

import java.io.IOException;
import java.util.List;

public interface GithubService {

	List<GHRepo> getRepos(ServiceUserInfo userInfo, GetReposCommand command) throws IOException;

	GHRepo getRepo(ServiceUserInfo userInfo) throws IOException;

	List<GHFile> getFilesRecursively(ServiceUserInfo userInfo, GetTreeCommand command) throws IOException;

	List<GHFile> getFilesFromRoot(ServiceUserInfo userInfo, GetFilesCommand command) throws IOException;

	List<GHFile> getFilesByTreeSha(ServiceUserInfo userInfo, GetFilesCommand command) throws IOException;
}
