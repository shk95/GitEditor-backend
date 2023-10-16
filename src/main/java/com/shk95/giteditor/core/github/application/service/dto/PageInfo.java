package com.shk95.giteditor.core.github.application.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PageInfo {

	private int pageAt;
	private int pageSize = 10;
}
