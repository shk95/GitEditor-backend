package com.shk95.giteditor.domain.common.security.service;

import com.shk95.giteditor.domain.common.constant.ProviderType;
import com.shk95.giteditor.domain.common.exception.OAuthUserNotRegisteredException;
import com.shk95.giteditor.domain.common.model.AbstractOAuth2UserInfo;
import com.shk95.giteditor.domain.model.provider.Provider;
import com.shk95.giteditor.domain.model.provider.ProviderId;
import com.shk95.giteditor.domain.model.provider.ProviderRepository;
import com.shk95.giteditor.domain.model.user.CustomUserDetails;
import com.shk95.giteditor.domain.model.user.oauth.OAuth2UserInfoFactory;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.shk95.giteditor.config.ConstantFields.OAuthService.PROVIDER_ACCESS_TOKEN;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final ProviderRepository providerRepository;

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

	private OAuth2User authProcess(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
		ProviderType providerType
			= ProviderType.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase());
		String retrievedAccessToken = oAuth2UserRequest.getAccessToken().getTokenValue();
		Map<String, String> additionalAttributes = new HashMap<>();
		additionalAttributes.put(PROVIDER_ACCESS_TOKEN, retrievedAccessToken);
		log.debug("#### oAuth2userInfo accessToken : [{}]", retrievedAccessToken);
		log.debug("#### oAuth2userInfo getAttributes : [{}]\n", oAuth2User.getAttributes());
		log.debug("#### oAuth2userInfo clientRegistration : [{}]\n", oAuth2UserRequest.getClientRegistration());

		// OAuth 서비스를 통해 로그인성공후 가져온 사용자의 정보를 가져옴.
		AbstractOAuth2UserInfo retrievedUserInfo = OAuth2UserInfoFactory
			.getOAuth2UserInfo(providerType, oAuth2User.getAttributes(), additionalAttributes);

		// oAuth 가입정보의 pk(provider type, provider id)
		ProviderId providerId = new ProviderId(providerType, retrievedUserInfo.getId());
		Provider oAuthUser = providerRepository.findById(providerId).orElseThrow(
			() -> new OAuthUserNotRegisteredException("서비스에 가입되지 않은 oAuth2 로그인 유저. 가입 필요.", null, retrievedUserInfo));

		CustomUserDetails userDetails =
			CustomUserDetails.createUserDetailsOfOAuthUser(oAuthUser.getUser(), oAuth2User.getAttributes()).build();
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);

		updateUserInfo(oAuthUser, retrievedUserInfo);// jpa 변경감지 이용 업데이트
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
