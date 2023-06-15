package com.shk95.giteditor.domain.model.document;

import com.shk95.giteditor.domain.application.DocumentService;
import com.shk95.giteditor.domain.application.GithubService;
import com.shk95.giteditor.domain.application.commands.document.CreateDocumentCommand;
import com.shk95.giteditor.domain.application.commands.github.CreateFileCommand;
import com.shk95.giteditor.domain.model.github.GithubFileMode;
import com.shk95.giteditor.domain.model.github.ServiceUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class MarkdownService {

	private final DocumentService documentService;
	private final GithubService githubService;

	public CreatedFile createMarkdown(CreateDocumentCommand command) throws IOException {
		CreatedFile md = documentService.createDocumentAsMarkdown(command);
		githubService.createFile(ServiceUserInfo.userId(command.getUserId()),
			CreateFileCommand.builder()
				.repoName(command.getRepoName())
				.branchName(command.getBranchName())
				.mode(GithubFileMode.RW_BLOB)
				.baseTreeSha(command.getBaseTreeSha())
				.content(md.getContent())
				.commitMessage(command.getCommitMessage())
				.filename(command.getFilename())
				.basePath(command.getBasePath()).build());
		return md;
	}
}
