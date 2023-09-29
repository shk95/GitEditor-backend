package com.shk95.giteditor.core.github.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class GithubBranch {
	private String branchName;
	private String branchSha;

	GithubBranch() {
	}
}
