package com.shk95.giteditor.core.openai.application.service;


import lombok.Builder;
import lombok.Getter;

@Getter
public class SortInfo {

	public enum Direction {ASC, DESC}

	private final Direction direction;
	private final String property;

	@Builder
	private SortInfo(Direction direction, String property) {
		this.direction = direction;
		this.property = property;
	}
}
