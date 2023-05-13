package com.shk95.giteditor.domain.common.security.service;

import com.shk95.giteditor.domain.common.security.UserDetailsImpl;
import com.shk95.giteditor.domain.common.security.info.OAuth2UserInfo;
import com.shk95.giteditor.domain.common.security.info.OAuth2UserInfoFactory;
import com.shk95.giteditor.domain.common.security.oauth.ProviderType;
import com.shk95.giteditor.domain.model.roles.Role;
import com.shk95.giteditor.domain.model.user.User;
import com.shk95.giteditor.domain.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;

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
		log.debug("oauth retrieved access token value : [{}]", retrievedAccessToken);

		OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, oAuth2User.getAttributes());
		Optional<User> savedUser = userRepository.findByProviderEmail(userInfo.getEmail());

		if (!savedUser.isPresent()) {
			this.createUser(userInfo, providerType);
			throw new UsernameNotFoundException("Cannot find user.");// oauth 인증 성공. 하지만 가입된 사용자는 아니다.
		} /*else {//TODO: oAuth: authProcess: 검토
			if (providerType != savedUser.get().getProviderType()) {
				throw new OAuthProviderMissMatchException(
					"Looks like you're signed up with " + providerType +
						" account. Please use your " + savedUser.get().getProviderType() + " account to login."
				);
			}
			updateUser(savedUser.get(), userInfo);
		}*/
		return UserDetailsImpl.createUserDetailsBuilder(savedUser.get(), oAuth2User.getAttributes()).build();
	}

	private void createUser(OAuth2UserInfo userInfo, ProviderType providerType) {
		userRepository.saveAndFlush(User.builder()
			.userId(userInfo.getId())// TODO: oauth: createUser: userId 중복성 문제
			.username(userInfo.getName())
			.defaultEmail(userInfo.getEmail())
			.providerEmail(userInfo.getEmail())
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
