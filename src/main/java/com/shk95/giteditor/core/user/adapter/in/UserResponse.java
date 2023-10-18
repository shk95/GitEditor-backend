package com.shk95.giteditor.core.user.adapter.in;

import com.shk95.giteditor.common.constant.ProviderType;
import com.shk95.giteditor.common.security.Role;
import com.shk95.giteditor.core.user.domain.provider.Provider;
import com.shk95.giteditor.core.user.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

public class UserResponse {

	@Getter
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

		@Builder
		public Me(Collection<? extends GrantedAuthority> authorities, String userId, Role role, ProviderType providerType, String defaultEmail, String defaultUsername, String defaultImgUrl, String providerEmail, String providerLoginId, String providerUsername, String providerImgUrl, boolean githubEnabled, boolean openAIEnabled, Map<String, Object> attributes) {
			this.authorities = authorities;
			this.userId = userId;
			this.role = role;
			this.providerType = providerType;
			this.defaultEmail = defaultEmail;
			this.defaultUsername = defaultUsername;
			this.defaultImgUrl = defaultImgUrl;
			this.providerEmail = providerEmail;
			this.providerLoginId = providerLoginId;
			this.providerUsername = providerUsername;
			this.providerImgUrl = providerImgUrl;
			this.githubEnabled = githubEnabled;
			this.openAIEnabled = openAIEnabled;
			this.attributes = attributes;
		}

		public static Me from(User user) {
			Provider provider = user.getProviders().stream()
				.filter(p -> user.getUserId().getProviderType() == p.getProviderId().getProviderType())
				.findFirst().orElseGet(Provider::new);
			return Me.builder()
				.userId(user.getUserId().getUserLoginId())
				.role(user.getRole())
				.providerType(user.getUserId().getProviderType())
				.defaultEmail(user.getDefaultEmail())
				.defaultUsername(user.getUsername())
				.defaultImgUrl(user.getProfileImageUrl())

				.providerEmail(provider.getProviderEmail())
				.providerLoginId(provider.getProviderLoginId())
				.providerUsername(provider.getProviderUserName())
				.providerImgUrl(provider.getProviderImgUrl())

				.githubEnabled(user.isGithubEnabled())
				.openAIEnabled(user.isOpenAIEnabled()).build();
		}
	}

	@Builder
	@Getter
	public static class Profile {

		private String uploadedImageUrl;
	}
}
