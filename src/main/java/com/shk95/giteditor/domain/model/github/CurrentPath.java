package com.shk95.giteditor.domain.model.github;

import lombok.Getter;

import java.util.List;

@Getter
public class CurrentPath {
	private List<GithubFile> filesInPath;
}
