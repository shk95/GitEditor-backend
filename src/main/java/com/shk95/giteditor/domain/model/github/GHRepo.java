package com.shk95.giteditor.domain.model.github;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class GHRepo {

	private String repoFullName;
	private String repoName;
	private List<GHBranch> branches;
	private String url;
	private String htmlUrl;
	private String defaultBranch;
	private GHOwner owner;
}
