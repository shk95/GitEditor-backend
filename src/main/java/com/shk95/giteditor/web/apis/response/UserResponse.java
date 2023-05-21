package com.shk95.giteditor.web.apis.response;

import com.shk95.giteditor.domain.common.constant.ProviderType;
import com.shk95.giteditor.domain.common.security.Role;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

public class UserResponse {

	@Getter
	@Builder
	public static class Me {
		private Collection<? extends GrantedAuthority> authorities;
		private String userId;
		private Role role;
		private ProviderType providerType;
		private String defaultEmail;
		private String defaultUsername;
		private String defaultImgUrl;//default profile image
		private String providerEmail;
		private String providerLoginId;
		private String providerUsername;
		private String providerImgUrl;
		private Map<String, Object> attributes;
	}
}
