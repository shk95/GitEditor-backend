package com.shk95.giteditor.domain.model.github;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public enum GHFileMode {
	RW_BLOB("100644", "file"), X_BLOB("100755", "executable"),
	TREE("040000", "subdirectory"), COMMIT("160000", "submodule"),
	SYMLINK("120000", "a blob that specifies the path of a symlink");

	private final String code;
	private final String description;

	public static GHFileMode fromCode(String code) {
		return Arrays.stream(GHFileMode.values())
			.filter(e -> e.getCode().equals(code)).findAny().orElseThrow(IllegalArgumentException::new);
	}

	@JsonValue
	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}
}
