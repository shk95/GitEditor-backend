package com.shk95.giteditor.domain.application.commands.document;

import com.shk95.giteditor.domain.model.document.Extension;
import com.shk95.giteditor.domain.model.document.StorageType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateDocumentCommand {

	private String url;

	private StorageType storageType;
	private Extension extension;
	private String repoName;
	private String path;
	private String fileName;
}
