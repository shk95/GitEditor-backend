package com.shk95.giteditor.domain.application.impl;

import com.groupdocs.cloud.conversion.api.ConvertApi;
import com.groupdocs.cloud.conversion.client.ApiException;
import com.groupdocs.cloud.conversion.client.Configuration;
import com.groupdocs.cloud.conversion.model.ConvertSettings;
import com.groupdocs.cloud.conversion.model.WebLoadOptions;
import com.groupdocs.cloud.conversion.model.requests.ConvertDocumentRequest;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DocumentServiceImplTest {

	public static void main(String[] args) throws IOException {
		/*File a = fetchHTMLBody("naver.com");

		get(a);*/
		get(fetchHTMLBody("https://www.google.com/search?q=GroupDocs.Conversion+html+to+md&oq=GroupDocs.Conversion+html+to+md&aqs=edge..69i57j0i546j69i64.8918j0j1&sourceid=chrome&ie=UTF-8"));

//		System.out.println(a);
	}

	public static String fetchHTMLBody(String host) throws IOException {
//		String encodedURL = "http://" + URLEncoder.encode(host, StandardCharsets.UTF_8.toString());
		Document doc = Jsoup.connect(host).get();
//		String html = doc.html();
		String html = doc.body().html();

		/*File outputFile = new File("output.html");
		PrintWriter writer = new PrintWriter(outputFile);
		writer.print(html);
		writer.close();*/

		return html;
	}

	public static void get(File html) throws ApiException {
		String ClientId = "3fecd76c-5e8b-4329-8b8f-018dad48c0d6";
		String ClientSecret = "10ecc0f39bf391e6f0a01b56d2bc1311";
		Configuration configuration = new Configuration(ClientId, ClientSecret);

		// Create an instance of the convert API
		ConvertApi apiInstance = new ConvertApi(configuration);

		WebLoadOptions loadOptions = new WebLoadOptions();
		loadOptions.setFormat("html");

		ConvertSettings settings = new ConvertSettings();
		settings.setFilePath("");
		settings.setFormat("md");
		settings.setOutputPath("ConvertedFiles");

		List response = apiInstance.convertDocument(new ConvertDocumentRequest(settings));
/*
		ConvertDocumentDirectRequest request = new ConvertDocumentDirectRequest("md", html, 1, 0, loadOptions, null); // all pages

		File result = null;
		try {
			result = apiInstance.convertDocumentDirect(request);
		} catch (ApiException e) {
			throw new RuntimeException(e);
		}*/

		System.out.println("Document converted: " + response.get(0).toString());
	}

	public static void get(String html) throws IOException {

		OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse("application/json");
//		RequestBody body = RequestBody.create(mediaType, "{\n    \"html\": \"<h1>Markdown text</h1>\"\n}");
		RequestBody body = RequestBody.create(mediaType, html);
		Request request = new Request.Builder()
			.url("https://html-to-gemtext-or-markdown.p.rapidapi.com/html2md/")
			.post(body)
			.addHeader("content-type", "application/json")
			.addHeader("X-RapidAPI-Key", "ac6bc12a9emsh4eda32b137c8cf2p1b0a2bjsn5de0e0843502")
			.addHeader("X-RapidAPI-Host", "html-to-gemtext-or-markdown.p.rapidapi.com")
			.build();

		Response response = client.newCall(request).execute();
		System.out.println(response.body().string());
	}

	@Test
	void createDocumentAsMarkdown() {
	}


}
