package com.shk95.giteditor.domain.model.roles;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Role {
	GUEST("ROLE_GUEST", "Guest User"),
	USER("ROLE_USER", "Granted User"),
	ADMIN("ROLE_ADMIN", "Admin User");

	private final String code;
	private final String description;

	public static Role of(String code) {
		return Arrays.stream(Role.values())
			.filter(r -> r.getCode().equals(code))
			.findAny()
			.orElse(GUEST);
	}
}
