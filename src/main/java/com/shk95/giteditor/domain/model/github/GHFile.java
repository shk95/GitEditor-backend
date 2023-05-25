package com.shk95.giteditor.domain.model.github;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class GHFile {
	private String sha;
	private GHFileType type;
	private String path; // relative file name
	private String url;
	private GHFileMode mode;
}
