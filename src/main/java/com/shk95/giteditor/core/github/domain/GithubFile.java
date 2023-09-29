package com.shk95.giteditor.core.github.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GithubFile {

	private String sha;
	private GithubFileType type;
	private String path; // relative file name
	private String url;
	private GithubFileMode mode;
	private long size;

	private String textContent; //plain text
}
