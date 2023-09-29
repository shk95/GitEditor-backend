package com.shk95.giteditor.core.openai.application.service;


import lombok.Builder;
import lombok.Getter;

@Getter
public class PageInfo {

	private final boolean isLast;
	private final long totalSize;
	private final int totalPage;

	@Builder
	private PageInfo(boolean isLast, long totalSize, int totalPage) {
		this.isLast = isLast;
		this.totalSize = totalSize;
		this.totalPage = totalPage;
	}
}
