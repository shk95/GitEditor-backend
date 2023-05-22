package com.shk95.giteditor.domain.application;

import com.shk95.giteditor.domain.application.commands.document.CreateDocumentCommand;
import com.shk95.giteditor.domain.model.document.CreatedFile;

import java.io.IOException;

public interface DocumentService {

	CreatedFile createDocumentAsMarkdown(CreateDocumentCommand command) throws IOException;
}
