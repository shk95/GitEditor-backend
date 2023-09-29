package com.shk95.giteditor.core.github.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class GithubRepo {

	private String repoFullName;
	private String repoName;
	private List<GithubBranch> branches;
	private String url;
	private String htmlUrl;
	private String defaultBranch;
	private GithubOwner owner;
}
