package com.shk95.giteditor.domain.common.security;

import com.shk95.giteditor.domain.model.roles.Role;
import com.shk95.giteditor.domain.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@AllArgsConstructor
@Builder
@Getter
public class UserDetailsImpl implements UserDetails, OAuth2User, OidcUser {
	private Collection<? extends GrantedAuthority> authorities;
	private String userId;
	private String defaultEmail;
	private String providerEmail;
	private String password;
	private String username;
	private Role role;
	private Map<String, Object> attributes;

	public static UserDetailsImplBuilder createUserDetailsBuilder(User user) {
		return UserDetailsImpl.builder()
			.userId(user.getUserId())
			.password(user.getPassword())
			.username(user.getUsername())
			.defaultEmail(user.getDefaultEmail())
			.role(Role.USER)
			.authorities(Collections.singletonList(new SimpleGrantedAuthority(Role.USER.getCode())));
	}

	public static UserDetailsImplBuilder createUserDetailsBuilder(User user, Map<String, Object> attributes) {
		UserDetailsImplBuilder userDetails = UserDetailsImpl.createUserDetailsBuilder(user);
		userDetails.attributes(attributes);
		return userDetails;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return userId;
	}

	@Override
	public String getName() {
		return userId;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Map<String, Object> getClaims() {
		return null;
	}

	@Override
	public OidcUserInfo getUserInfo() {
		return null;
	}

	@Override
	public OidcIdToken getIdToken() {
		return null;
	}
}
