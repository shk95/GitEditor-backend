package com.shk95.giteditor.core.github.domain;

import lombok.Getter;

import java.util.List;

@Getter
public class CurrentPath {
	private List<GithubFile> filesInPath;
}
