package com.shk95.giteditor.domain.common.security.service;

import com.shk95.giteditor.domain.common.constants.ProviderType;
import com.shk95.giteditor.domain.common.constants.Role;
import com.shk95.giteditor.domain.common.security.CustomUserDetails;
import com.shk95.giteditor.domain.common.security.info.OAuth2UserInfo;
import com.shk95.giteditor.domain.common.security.info.OAuth2UserInfoFactory;
import com.shk95.giteditor.domain.model.provider.Provider;
import com.shk95.giteditor.domain.model.provider.ProviderRepository;
import com.shk95.giteditor.domain.model.user.User;
import com.shk95.giteditor.domain.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.shk95.giteditor.config.ConstantFields.OAuthService.PROVIDER_ACCESS_TOKEN;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;
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
		//TODO: oauthProcess: 리펙토링
		String retrievedAccessToken = oAuth2UserRequest.getAccessToken().getTokenValue();
		log.debug("#### oAuth2userInfo accessToken : [{}]", retrievedAccessToken);
		log.debug("#### oAuth2userInfo getAttributes : [{}]\n", oAuth2User.getAttributes());
		log.debug("#### oAuth2userInfo clientRegistration : [{}]\n", oAuth2UserRequest.getClientRegistration());

		Map<String, String> additionalAttributes = new HashMap<>();
		additionalAttributes.put(PROVIDER_ACCESS_TOKEN, retrievedAccessToken);

		// OAuth 서비스를 통해 로그인성공후 가져온 사용자의 정보를 가져옴.
		OAuth2UserInfo userInfo = OAuth2UserInfoFactory
			.getOAuth2UserInfo(providerType, oAuth2User.getAttributes(), additionalAttributes);


		Optional<Provider> oAuthUser = providerRepository.findByProviderEmail(userInfo.getEmail());

		if (!oAuthUser.isPresent()) {
			this.createUser(userInfo, providerType);
		} /*else {//TODO: oAuth: authProcess: 검토
			if (providerType != oAuthUser.get().getProviderType()) {
				throw new OAuthProviderMissMatchException(
					"Looks like you're signed up with " + providerType +
						" account. Please use your " + oAuthUser.get().getProviderType() + " account to login."
				);
			}
			updateUser(oAuthUser.get(), userInfo);
		}*/
		return CustomUserDetails.createUserDetailsBuilder(oAuthUser.get(), oAuth2User.getAttributes()).build();
	}

	private void createUser(OAuth2UserInfo userInfo, ProviderType providerType) {
		userRepository.saveAndFlush(User.builder()
			.userId(userInfo.getId())// TODO: oauth: createUser: userId 중복성 문제
			.username(userInfo.getName())
			.defaultEmail(userInfo.getEmail())
			.providerType(providerType)
			.role(Role.USER)
			.build());
	}

	private void updateUserInfo(User user, OAuth2UserInfo userInfo) {
		if (userInfo.getName() != null && !user.getUsername().equals(userInfo.getName())) {
			user.updateUserName(userInfo.getName());
		}
		if (userInfo.getImageUrl() != null && !user.getProfileImageUrl().equals(userInfo.getImageUrl())) {
			user.updateProfileImageUrl(userInfo.getImageUrl());
		}
	}
}
