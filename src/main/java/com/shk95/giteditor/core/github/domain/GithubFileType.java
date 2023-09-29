package com.shk95.giteditor.core.github.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public enum GithubFileType {
	COMMIT("commit", "submodule"), BLOB("blob", "file"), TREE("tree", "directory");

	private final String code;
	private final String description;

	public static GithubFileType fromCode(String code) {
		return Arrays.stream(GithubFileType.values())
			.filter(e -> e.getCode().equals(code)).findAny().orElseThrow(IllegalArgumentException::new);
	}

	@JsonValue
	public String getCode() {
		return this.code;
	}

	public String getDescription() {
		return this.description;
	}
}
