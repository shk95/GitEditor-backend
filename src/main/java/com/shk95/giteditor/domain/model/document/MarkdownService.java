package com.shk95.giteditor.domain.model.document;

import com.shk95.giteditor.domain.application.DocumentService;
import com.shk95.giteditor.domain.application.GithubService;
import com.shk95.giteditor.domain.application.commands.document.CreateDocumentCommand;
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
		return md;
	}
}
