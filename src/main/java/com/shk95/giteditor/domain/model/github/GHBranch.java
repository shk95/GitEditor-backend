package com.shk95.giteditor.domain.model.github;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class GHBranch {
	private String branchName;
	private String branchSha;

	GHBranch() {
	}
}
