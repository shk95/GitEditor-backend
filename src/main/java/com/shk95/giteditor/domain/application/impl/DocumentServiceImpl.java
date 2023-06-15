package com.shk95.giteditor.domain.application.impl;

import com.shk95.giteditor.domain.application.DocumentService;
import com.shk95.giteditor.domain.application.commands.document.CreateDocumentCommand;
import com.shk95.giteditor.domain.model.document.CreatedFile;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.util.data.MutableDataSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class DocumentServiceImpl implements DocumentService {

	@Override
	public CreatedFile createDocumentAsMarkdown(CreateDocumentCommand command) throws IOException {
		Document document = Jsoup.connect(command.getUrl()).userAgent("Mozilla/5.0").referrer("https://www.google.com").get();
		String html = document.html();
		MutableDataSet options = new MutableDataSet();
		FlexmarkHtmlConverter converter = FlexmarkHtmlConverter.builder(options).build();
		String markdown = converter.convert(html);
		return CreatedFile.builder()
			.storageType(command.getStorageType())
			.repoName(command.getRepoName())
			.content(markdown)
			.path(command.getBasePath())
			.fileName(command.getFilename())
			.extension(command.getExtension())
			.build();
	}
}
