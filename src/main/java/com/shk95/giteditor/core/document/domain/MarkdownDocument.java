package com.shk95.giteditor.core.document.domain;

import lombok.Getter;

@Getter
public class MarkdownDocument extends Document {

	private MarkdownDocument(String content) {
		super(content, Extension.MD);
	}

	public static MarkdownDocument create(String content) {
		return new MarkdownDocument(content);
	}
}
