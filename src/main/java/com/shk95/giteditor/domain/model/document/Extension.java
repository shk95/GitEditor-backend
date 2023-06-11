package com.shk95.giteditor.domain.model.document;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Extension {
	MD("md");

	private final String extension;
}
