package com.shk95.giteditor.domain.model.document;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreatedFile {

	private String content;
	private String path;
	private String repoName;
	private StorageType storageType;
	private String fileName;
	private String extension;
}
