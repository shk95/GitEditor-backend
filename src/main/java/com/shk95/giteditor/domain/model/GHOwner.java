package com.shk95.giteditor.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class GHOwner {

	private String name;
	private String loginId;
	private String avatarUrl;
	private String email;
	private String type;
}
