/*
package com.shk95.giteditor.domain.model.github;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHBlob;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
@Getter
public class GithubBlob {

	private String textContent;
	private byte[] blobContent;
	private ReadableType readableType;

	public GithubBlob(GHBlob ghBlob) {
		StringBuilder builder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(ghBlob.read(), StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
		} catch (Exception e) {
			log.warn("Unexpected exception occurred while reading blob as string. {}", e.getMessage());
			builder = null;
		}
		if (builder == null) {
			this.readableType = ReadableType.BINARY;
			this.blobContent =
		} else {

		}
		this.readableType = ReadableType.TEXT;
	}

	//string to byte

}
*/
