package com.shk95.giteditor.domain.model.github;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public enum GHFileType {
	COMMIT("commit", "submodule"), BLOB("blob", "file"), TREE("tree", "directory");

	private final String code;
	private final String description;

	public static GHFileType fromCode(String code) {
		return Arrays.stream(GHFileType.values())
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
