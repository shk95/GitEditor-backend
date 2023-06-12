package com.shk95.giteditor.domain.model.github;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class GithubOwner {

	private String name;
	private String loginId;
	private String avatarUrl;
	private String email;
	private String type;
}
