package com.shk95.giteditor.core.auth.domain;

import com.shk95.giteditor.common.constant.ProviderType;
import com.shk95.giteditor.core.user.domain.user.User;
import com.shk95.giteditor.core.user.domain.user.UserId;
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
	private String providerTypeAndLoginId; // ProviderType + userId
	private String password;
	//	private Role role;
	private ProviderType providerType;
	private String defaultEmail;
	private String defaultUsername;
	//	private String defaultImgUrl;//default profile image
	/*private String providerEmail;
	private String providerLoginId;
	private String providerUsername;
	private String providerImgUrl;*/
//	private String openAIAccessToken;
	private Map<String, Object> attributes;

	private boolean isUserEmailVerified;
	private boolean isUserEnabled;

	private boolean isGithubEnabled;// TODO: 권한으로 변경
	private boolean isOpenAIEnabled;


	/**
	 * 일반가입 사용자
	 *
	 * @param user 기본 사용자 정보
	 * @return userDetails : 인증된 사용자의 정보
	 */
	public static CustomUserDetails of(User user) {
		CustomUserDetailsBuilder userDetailsBuilder = CustomUserDetails.builder()
			.providerTypeAndLoginId(user.getUserId().get())
			.password(user.getPassword())
//			.role(user.getRole())
			.authorities(Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getCode())))
			.providerType(user.getUserId().getProviderType())
			.defaultUsername(user.getUsername())
			.defaultEmail(user.getDefaultEmail())
//			.defaultImgUrl(user.getProfileImageUrl())
			.isGithubEnabled(user.isGithubEnabled())
			.isOpenAIEnabled(user.isOpenAIEnabled())
//			.openAIAccessToken(user.getOpenAIToken())
			.isUserEnabled(user.isUserEnabled());
		/*if (!user.getProviders().isEmpty()) {
			userDetailsBuilder
				.providerEmail(user.getProviders().get(0).getProviderEmail())
				.providerUsername(user.getProviders().get(0).getProviderUserName())
				.providerImgUrl(user.getProviders().get(0).getProviderImgUrl())
				.providerLoginId(user.getProviders().get(0).getProviderLoginId());
		}*/
		return userDetailsBuilder.build();
	}

	/**
	 * oAuth2 로 가입한 사용자
	 *
	 * @param user       기본 사용자 정보
	 * @param attributes oAuth2 제공 속성
	 * @return userDetailsBuilder : 인증된 사용자의 정보
	 */
	/*public static CustomUserDetailsBuilder createUserDetailsOfOAuthUser(User user, Map<String, Object> attributes) {
		// oAuth2 로 가입시의 기본 ProviderType 을 통해 찾은 정보
		Provider providerInfo = user.getProviders().stream()
			.filter(entity -> entity.getProviderId().getProviderType() == user.getUserId().getProviderType())
			.findFirst().orElseThrow(DefaultOAuthAccountNotFoundException::new);

		return CustomUserDetails.builder()
			.providerTypeAndLoginId(user.getUserId().getUserLoginId())
//			.role(user.getRole())
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
	}*/
	public UserId getUserId() {
		return UserId.of(this.providerTypeAndLoginId);
	}

	@Override
	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {// UserDetails 에서 사용하는 사용자 ID
		return this.getProviderTypeAndLoginId();
	}

	@Override
	public String getName() {// UserDetails 에서 사용하는 사용자 ID
		return this.getProviderTypeAndLoginId();
	}

	@Override
	public boolean isAccountNonExpired() {
		return this.isUserEnabled();
	}

	@Override
	public boolean isAccountNonLocked() {
		return this.isUserEnabled();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return this.isUserEnabled();
	}

	@Override
	public boolean isEnabled() {
		return this.isUserEnabled();
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
