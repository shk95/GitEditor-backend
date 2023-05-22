package com.shk95.giteditor.domain.application;

import com.shk95.giteditor.domain.application.commands.document.CreateDocumentCommand;
import com.shk95.giteditor.domain.application.impl.DocumentServiceImpl;
import com.shk95.giteditor.domain.model.document.CreatedFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class DocumentServiceTest {

	private DocumentServiceImpl documentService;

	@BeforeEach
	public void setup() {
		documentService = new DocumentServiceImpl();
	}

	@Test
	public void testCreateDocumentAsMarkdown() throws IOException {
		String url = "https://jojoldu.tistory.com/category/생각정리";

		CreateDocumentCommand command = CreateDocumentCommand.builder().url(url).build();
		CreatedFile result = documentService.createDocumentAsMarkdown(command);

		System.out.println(result.getContent());
	}
}
