package com.shk95.giteditor.domain.model.user;

import com.shk95.giteditor.domain.common.constant.ProviderType;
import com.shk95.giteditor.domain.common.exception.DefaultOAuthAccountNotFoundException;
import com.shk95.giteditor.domain.common.security.Role;
import com.shk95.giteditor.domain.model.provider.Provider;
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
public class CustomUserDetails implements UserDetails, OAuth2User, OidcUser {

	private Collection<? extends GrantedAuthority> authorities;
	private String userId;
	private String password;
	private Role role;
	private ProviderType providerType;
	private String defaultEmail;
	private String defaultUsername;
	private String defaultImgUrl;//default profile image
	private boolean isGithubEnabled;
	private boolean isOpenAIEnabled;
	private String providerEmail;
	private String providerLoginId;
	private String providerUsername;
	private String providerImgUrl;
	private String openAIAccessToken;
	private Map<String, Object> attributes;

	private boolean isUserEmailVerified;
	private boolean isUserEnabled;

	/**
	 * 일반가입 사용자
	 *
	 * @param user 기본 사용자 정보
	 * @return userDetails : 인증된 사용자의 정보
	 */
	public static CustomUserDetails of(User user) {
		CustomUserDetailsBuilder userDetailsBuilder = CustomUserDetails.builder()
			.userId(user.getUserId().getUserLoginId())
			.password(user.getPassword())
			.role(user.getRole())
			.authorities(Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getCode())))
			.providerType(user.getUserId().getProviderType())
			.defaultUsername(user.getUsername())
			.defaultEmail(user.getDefaultEmail())
			.defaultImgUrl(user.getProfileImageUrl())
			.isGithubEnabled(user.isGithubEnabled())
			.isOpenAIEnabled(user.isOpenAIEnabled())
			.openAIAccessToken(user.getOpenAIToken())
			.isUserEnabled(user.isUserEnabled());
		if (!user.getProviders().isEmpty()) {
			userDetailsBuilder
				.providerEmail(user.getProviders().get(0).getProviderEmail())
				.providerUsername(user.getProviders().get(0).getProviderUserName())
				.providerImgUrl(user.getProviders().get(0).getProviderImgUrl())
				.providerLoginId(user.getProviders().get(0).getProviderLoginId());
		}
		return userDetailsBuilder.build();
	}

	/**
	 * oAuth2 로 가입한 사용자
	 *
	 * @param user       기본 사용자 정보
	 * @param attributes oAuth2 제공 속성
	 * @return userDetailsBuilder : 인증된 사용자의 정보
	 */
	public static CustomUserDetailsBuilder createUserDetailsOfOAuthUser(User user, Map<String, Object> attributes) {
		// oAuth2 로 가입시의 기본 ProviderType 을 통해 찾은 정보
		Provider providerInfo = user.getProviders().stream()
			.filter(entity -> entity.getProviderId().getProviderType() == user.getUserId().getProviderType())
			.findFirst().orElseThrow(DefaultOAuthAccountNotFoundException::new);

		return CustomUserDetails.builder()
			.userId(user.getUserId().getUserLoginId())
			.role(user.getRole())
			.authorities(Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getCode())))
			.providerType(user.getUserId().getProviderType())
			.defaultUsername(user.getUsername())
			.defaultEmail(user.getDefaultEmail())
			.defaultImgUrl(user.getProfileImageUrl())
			.isGithubEnabled(user.isGithubEnabled())
			.isOpenAIEnabled(user.isOpenAIEnabled())
			.openAIAccessToken(user.getOpenAIToken())
			.providerUsername(providerInfo.getProviderUserName())
			.providerImgUrl(providerInfo.getProviderImgUrl())
			.providerLoginId(providerInfo.getProviderLoginId())
			.providerEmail(providerInfo.getProviderEmail())
			.attributes(attributes);
	}

	public UserId getUserEntityId() {
		return new UserId(this.providerType, this.userId);
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
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
	public String getUsername() {// UserDetails 에서 사용하는 사용자 ID
		return userId;
	}

	@Override
	public String getName() {// UserDetails 에서 사용하는 사용자 ID
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
		return this.isUserEnabled;
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
