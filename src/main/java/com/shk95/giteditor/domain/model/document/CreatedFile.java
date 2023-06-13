package com.shk95.giteditor.domain.model.document;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreatedFile {

	private StorageType storageType;
	private String content;
	private String repoName;
	private String path;
	private String fileName;
	private Extension extension;
}
