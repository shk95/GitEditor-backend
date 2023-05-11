package com.shk95.giteditor.domain.common.security.service;

import com.shk95.giteditor.domain.common.security.UserDetailsImpl;
import com.shk95.giteditor.domain.common.security.exception.OAuthProviderMissMatchException;
import com.shk95.giteditor.domain.common.security.info.OAuth2UserInfo;
import com.shk95.giteditor.domain.common.security.info.OAuth2UserInfoFactory;
import com.shk95.giteditor.domain.common.security.oauth.ProviderType;
import com.shk95.giteditor.domain.model.user.User;
import com.shk95.giteditor.domain.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User user = super.loadUser(userRequest);

		try {
			return this.process(userRequest, user);
		} catch (AuthenticationException ex) {
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
		}
	}

	private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) {
		ProviderType providerType = ProviderType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());

		OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());
		User savedUser = userRepository.findByDefaultEmail(userInfo.getEmail())
			.orElseThrow(() -> new UsernameNotFoundException("No User's email Found."));

		if (savedUser != null) {
			if (providerType != savedUser.getProviderType()) {
				throw new OAuthProviderMissMatchException(
					"Looks like you're signed up with " + providerType +
						" account. Please use your " + savedUser.getProviderType() + " account to login."
				);
			}
			updateUser(savedUser, userInfo);
		} else {
			savedUser = createUser(userInfo, providerType);
		}

		return UserDetailsImpl.createUserDetailsBuilder(savedUser, user.getAttributes()).build();
	}

	private User createUser(OAuth2UserInfo userInfo, ProviderType providerType) {
		LocalDateTime now = LocalDateTime.now();
		User user = new User(
			userInfo.getId(),
			userInfo.getName(),
			userInfo.getEmail(),
			"Y",
			userInfo.getImageUrl(),
			providerType,
			Role.USER,
			now,
			now
		);

		return userRepository.saveAndFlush(user);
	}

	private User updateUser(User user, OAuth2UserInfo userInfo) {
		if (userInfo.getName() != null && !user.getUsername().equals(userInfo.getName())) {
			user.setUsername(userInfo.getName());
		}

		if (userInfo.getImageUrl() != null && !user.getProfileImageUrl().equals(userInfo.getImageUrl())) {
			user.setProfileImageUrl(userInfo.getImageUrl());
		}

		return user;
	}
}
