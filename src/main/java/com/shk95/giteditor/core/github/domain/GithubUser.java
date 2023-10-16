package com.shk95.giteditor.core.github.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class GithubUser {

	private String login, avatar_url;
	private String location, blog, email, bio, name, company, type, twitter_username;
	private Integer followers, following, public_repos, public_gists;

}
