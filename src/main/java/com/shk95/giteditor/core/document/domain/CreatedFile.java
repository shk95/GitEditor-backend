package com.shk95.giteditor.core.document.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreatedFile {

	private final Document document;
	private final StorageType storageType;
	private final String repoName;
	private final String path;
	private final String fileName;

	@Builder
	protected CreatedFile(Document document, StorageType storageType, String repoName, String path, String fileName) {
		this.document = document;
		this.storageType = storageType;
		this.repoName = repoName;
		this.path = path;
		this.fileName = fileName;
	}

	@Getter
	public enum StorageType {
		GITHUB,
		S3,
		LOCAL
	}
}
