package com.shk95.giteditor.domain.model.github;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GithubFile {

	private String sha;
	private GithubFileType type;
	private String path; // relative file name
	private String url;
	private String content; //plain text
	private GithubFileMode mode;
	private GithubBlob blob;
	private long size;
}
