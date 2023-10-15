package com.shk95.giteditor.core.document.application.port.out;

import com.shk95.giteditor.core.document.domain.MarkdownDocument;

import java.io.IOException;

public interface CrawlerPort {

	MarkdownDocument asMarkdown(String url) throws IOException;
}
