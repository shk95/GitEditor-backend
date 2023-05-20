package com.shk95.giteditor.domain.common.security.info;

import com.shk95.giteditor.domain.common.security.info.impl.*;
import com.shk95.giteditor.domain.common.constant.ProviderType;

import java.util.Map;
import java.util.Objects;

public class OAuth2UserInfoFactory {
	public static OAuth2UserInfo getOAuth2UserInfo(
		ProviderType providerType, Map<String, Object> attributes, Map<String, String> additionalAttributes) {
		/*case GOOGLE:
				return new GoogleOAuth2UserInfo(attributes,additionalAttributes);
			case FACEBOOK:
				return new FacebookOAuth2UserInfo(attributes,additionalAttributes);
			case NAVER:
				return new NaverOAuth2UserInfo(attributes,additionalAttributes);
			case KAKAO:
				return new KakaoOAuth2UserInfo(attributes,additionalAttributes);*/
		if (Objects.requireNonNull(providerType) == ProviderType.GITHUB) {
			return new GithubOAuth2UserInfo(attributes, additionalAttributes, providerType);
		}
		throw new IllegalArgumentException("Invalid Provider Type.");
	}
}
