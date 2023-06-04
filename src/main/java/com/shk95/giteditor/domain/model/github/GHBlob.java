package com.shk95.giteditor.domain.model.github;

import lombok.Getter;

import java.nio.charset.StandardCharsets;

@Getter
public class GHBlob {
	private final byte[] bytes;
	private final String string;

	public GHBlob(String input) {
		this.string = input;
		this.bytes = this.stringToBlob(input);
	}

	/**
	 * Convert String to byte array (Blob)
	 */
	public byte[] stringToBlob(String str) {
		return str.getBytes(StandardCharsets.UTF_8);
	}

	/**
	 * Convert byte array (Blob) to String
	 */
	public String blobToString(byte[] blob) {
		return new String(blob, StandardCharsets.UTF_8);
	}
}
