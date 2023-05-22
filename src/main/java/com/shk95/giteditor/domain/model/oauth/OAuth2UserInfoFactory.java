package com.shk95.giteditor.domain.model.oauth;

import com.shk95.giteditor.domain.common.constant.ProviderType;
import com.shk95.giteditor.domain.common.model.AbstractOAuth2UserInfo;

import java.util.Map;

public class OAuth2UserInfoFactory {
	public static AbstractOAuth2UserInfo getOAuth2UserInfo(
		ProviderType providerType, Map<String, Object> attributes, Map<String, String> additionalAttributes) {
		switch (providerType) {
			case GOOGLE:
				return new GoogleOAuth2UserInfo(attributes, additionalAttributes, providerType);
			case FACEBOOK:
				return new FacebookOAuth2UserInfo(attributes, additionalAttributes, providerType);
			case NAVER:
				return new NaverOAuth2UserInfo(attributes, additionalAttributes, providerType);
			case KAKAO:
				return new KakaoOAuth2UserInfo(attributes, additionalAttributes, providerType);
			case GITHUB:
				return new GithubOAuth2UserInfo(attributes, additionalAttributes, providerType);
		}
		throw new IllegalArgumentException("Invalid Provider Type.");
	}
}
