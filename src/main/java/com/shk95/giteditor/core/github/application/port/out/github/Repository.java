package com.shk95.giteditor.core.github.application.port.out.github;

import com.shk95.giteditor.core.github.application.service.command.CommitFilesCommand;
import com.shk95.giteditor.core.github.application.service.command.File;
import com.shk95.giteditor.core.github.application.service.command.GetFilesCommand;
import com.shk95.giteditor.core.github.domain.GithubCredential;
import com.shk95.giteditor.core.github.domain.GithubFile;

import java.io.IOException;
import java.util.List;

public interface Repository {

	List<GithubFile> getAll(GithubCredential credential, GetFilesCommand command) throws IOException;

	void create(GithubCredential credential, File.CreateCommand command) throws IOException;

	void delete(GithubCredential credential, File.DeleteCommand command) throws IOException;

	boolean commit(GithubCredential credential, CommitFilesCommand command) throws IOException;

	void deleteDirectory(GithubCredential credential, File.DeleteCommand command) throws IOException;

	GithubFile get(GithubCredential credential, GetFilesCommand command) throws IOException;

	GithubFile get(GithubCredential credential, File.ReadCommand command) throws IOException;

	void update(GithubCredential credential, File.UpdateCommand command) throws IOException;

}
