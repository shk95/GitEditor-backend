package com.shk95.giteditor.core.document.application.service;

import com.shk95.giteditor.core.document.application.port.in.CrawlAndSaveUseCase;
import com.shk95.giteditor.core.document.application.port.out.CrawlerPort;
import com.shk95.giteditor.core.document.application.port.out.GithubRepositoryPort;
import com.shk95.giteditor.core.document.domain.CreatedFile;
import com.shk95.giteditor.core.document.domain.MarkdownDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class DocumentService implements CrawlAndSaveUseCase {

	private final CrawlerPort crawler;
	private final GithubRepositoryPort githubRepositoryPort;

	@Override
	public CreatedFile asMarkdown(CreateDocumentDto dto) throws IOException {
		MarkdownDocument md = crawler.asMarkdown(dto.url());
		return githubRepositoryPort.save(
			dto.userId(),
			CreatedFile.builder()
				.document(md)
				.storageType(dto.storageType())
				.repoName(dto.repoName())
				.path(dto.path()).build(),
			dto.toGithubDto()
		);
	}
}
