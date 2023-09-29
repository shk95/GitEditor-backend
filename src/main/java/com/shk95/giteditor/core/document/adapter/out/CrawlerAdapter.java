package com.shk95.giteditor.core.document.adapter.out;

import com.shk95.giteditor.core.document.application.port.out.CrawlerPort;
import com.shk95.giteditor.core.document.domain.MarkdownDocument;
import com.shk95.giteditor.core.document.infrastructure.Crawler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.shk95.giteditor.core.document.infrastructure.Crawler.get;

@Component
public class CrawlerAdapter implements CrawlerPort {

	@Override
	public MarkdownDocument asMarkdown(String url) throws IOException {
		String md = Crawler.asMarkdown(get(url));
		return MarkdownDocument.create(md);
	}
}
