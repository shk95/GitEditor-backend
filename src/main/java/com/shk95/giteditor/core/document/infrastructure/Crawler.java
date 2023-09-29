package com.shk95.giteditor.core.document.infrastructure;

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Crawler {

	private static final String REFERRER = "https://www.google.com";
	private static final String AGENT = "Mozilla/5.0";

	public static Document get(String url) throws IOException {
		return Jsoup.connect(url).userAgent(AGENT).referrer(REFERRER).get();
	}

	public static String asMarkdown(Document document) throws IOException {
		String html = document.html();
		MutableDataSet options = new MutableDataSet();
		FlexmarkHtmlConverter converter = FlexmarkHtmlConverter.builder(options).build();
		return converter.convert(html);
	}
}
