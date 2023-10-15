package com.shk95.giteditor.core.document.domain;

import lombok.Getter;

@Getter
public abstract class Document {

	private final String content;
	private final Extension extension;

	protected Document(String content, Extension extension) {
		this.content = content;
		this.extension = extension;
	}

	@Getter
	public enum Extension {
		MD("md");

		private final String extension;

		Extension(String extension) {
			this.extension = extension;
		}
	}
}
