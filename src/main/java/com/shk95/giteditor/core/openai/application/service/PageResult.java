package com.shk95.giteditor.core.openai.application.service;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PageResult<T> {

	private final List<T> items;
	private final PageInfo pageInfo;

	@Builder
	private PageResult(List<T> items, PageInfo pageInfo) {
		this.items = items;
		this.pageInfo = pageInfo;
	}

}
