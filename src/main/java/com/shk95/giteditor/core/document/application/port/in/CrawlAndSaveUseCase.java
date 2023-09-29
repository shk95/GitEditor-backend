package com.shk95.giteditor.core.document.application.port.in;

import com.shk95.giteditor.core.document.application.service.CreateDocumentDto;
import com.shk95.giteditor.core.document.domain.CreatedFile;

import java.io.IOException;

public interface CrawlAndSaveUseCase {

	CreatedFile asMarkdown(CreateDocumentDto dto) throws IOException;
}
