package com.shk95.giteditor.web.apis.response;

import com.shk95.giteditor.domain.common.constant.ProviderType;
import com.shk95.giteditor.domain.common.security.Role;
import com.shk95.giteditor.domain.model.user.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Map;

public class UserResponse {

	@Getter
	@AllArgsConstructor
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
		private boolean githubEnabled;
		private boolean openAIEnabled;
		private Map<String, Object> attributes;

		public Me() {
		}

		public Me(UserDetails userDetails) {
			CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
			this.userId = customUserDetails.getUserId();
			this.role = customUserDetails.getRole();
			this.providerType = customUserDetails.getProviderType();
			this.defaultEmail = customUserDetails.getDefaultEmail();
			this.defaultUsername = customUserDetails.getUsername();
			this.defaultImgUrl = customUserDetails.getDefaultImgUrl();
			this.providerEmail = customUserDetails.getProviderEmail();
			this.providerLoginId = customUserDetails.getProviderLoginId();
			this.providerUsername = customUserDetails.getProviderUsername();
			this.providerImgUrl = customUserDetails.getProviderImgUrl();
			this.githubEnabled = customUserDetails.isGithubEnabled();
			this.openAIEnabled = customUserDetails.isOpenAIEnabled();
		}
	}

	@Builder
	@Getter
	public static class Profile {

		private String uploadedImageUrl;
	}
}
