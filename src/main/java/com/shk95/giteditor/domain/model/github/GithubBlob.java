package com.shk95.giteditor.domain.model.github;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Getter
public class GithubBlob {

	private final String content;

	public GithubBlob(String rawContent) {
		this.content = rawContent;
	}

	public GithubBlob(InputStream inputStream) {
		StringBuilder builder = new StringBuilder();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
		} catch (IOException e) {
			// handle exception
		}
		this.content = builder.toString();
	}

	//string to byte

}
