package com.shk95.giteditor.domain.model.roles;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@AllArgsConstructor
public enum Authority implements GrantedAuthority {
	ROLE_USER("ROLE_USER"), ROLE_ADMIN("ROLE_ADMIN");

	private final String authority;

	@Override
	public String getAuthority() {
		return authority;
	}
}
