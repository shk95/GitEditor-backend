package com.shk95.giteditor.domain.model.roles;

import lombok.Getter;

@Getter
public enum Authority {
	ROLE_USER("USER"), ROLE_ADMIN("ADMIN");
	private final String description;

	Authority(String description) {
		this.description = description;
	}
}
