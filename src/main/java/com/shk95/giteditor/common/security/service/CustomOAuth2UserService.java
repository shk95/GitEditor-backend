package com.shk95.giteditor.common.security.service;

import com.shk95.giteditor.common.constant.ProviderType;
import com.shk95.giteditor.common.exception.OAuthUserNotRegisteredException;
import com.shk95.giteditor.common.model.AbstractOAuth2UserInfo;
import com.shk95.giteditor.core.auth.application.port.out.LoadUserPort;
import com.shk95.giteditor.core.auth.domain.CustomUserDetails;
import com.shk95.giteditor.core.user.application.port.out.ProviderRepositoryPort;
import com.shk95.giteditor.core.user.domain.provider.Provider;
import com.shk95.giteditor.core.user.domain.provider.ProviderId;
import com.shk95.giteditor.core.user.domain.user.UserId;
import com.shk95.giteditor.core.user.domain.user.oauth.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.shk95.giteditor.config.Constants.OAuthService.PROVIDER_ACCESS_TOKEN;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Component
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final ProviderRepositoryPort providerRepositoryPort;
	private final LoadUserPort loadUserPort;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User user = super.loadUser(userRequest);
		try {
			return this.authProcess(userRequest, user);
		} catch (AuthenticationException ex) {
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
		}
	}

	private OAuth2User authProcess(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {// TODO: OAuth 인증토큰 관련 개선
		ProviderType providerType
			= ProviderType.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase());
		String retrievedAccessToken = oAuth2UserRequest.getAccessToken().getTokenValue();
		Map<String, String> additionalAttributes = new HashMap<>();
		additionalAttributes.put(PROVIDER_ACCESS_TOKEN, retrievedAccessToken);
		log.debug("#### oAuth2userInfo accessToken : [{}]", retrievedAccessToken);
		log.debug("#### oAuth2userInfo getAttributes : [{}]\n", oAuth2User.getAttributes());
		log.debug("#### oAuth2userInfo clientRegistration : [{}]\n", oAuth2UserRequest.getClientRegistration());

		// 로그인성공후 OAuth 서비스 사용자의 정보를 가져옴.
		AbstractOAuth2UserInfo retrievedUserInfo = OAuth2UserInfoFactory
			.getOAuth2UserInfo(providerType, oAuth2User.getAttributes(), additionalAttributes);

		// OAuth 서비스 정보를 DB 에서 확인 pk(provider type, provider id)
		String oAuthServiceId = retrievedUserInfo.getId();
		ProviderId providerId = new ProviderId(providerType, oAuthServiceId);
		Provider oAuthUser = providerRepositoryPort.findById(providerId)
			.map(provider -> {
				updateUserInfo(provider, retrievedUserInfo); // 최신화 TODO: 최신화과정을 단일 기능으로 추출.
				return provider;
			})
			.orElseThrow(
				() -> new OAuthUserNotRegisteredException("서비스에 가입되지 않은 oAuth2 로그인 유저. 가입 필요.", null, retrievedUserInfo)); // 회원가입으로 리다이렉트

		// security 인가
		CustomUserDetails userDetails = loadUserPort.loadUser(new UserId(providerType, oAuthServiceId));
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);

		return userDetails;
	}

	/**
	 * @param providerUser entity
	 * @param userInfo     오버라이딩된 oAuth user info
	 */
	private void updateUserInfo(Provider providerUser, AbstractOAuth2UserInfo userInfo) {
		Provider.update(providerUser, userInfo);
	}
}
