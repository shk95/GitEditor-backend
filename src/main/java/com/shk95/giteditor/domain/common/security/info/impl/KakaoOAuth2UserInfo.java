package com.shk95.giteditor.domain.common.security.info.impl;


import com.shk95.giteditor.domain.common.constant.ProviderType;
import com.shk95.giteditor.domain.common.security.info.OAuth2UserInfo;

import java.util.Map;

@Deprecated
public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

	public KakaoOAuth2UserInfo(Map<String, Object> attributes, Map<String, String> additionalAttributes, ProviderType providerType) {
		super(attributes, additionalAttributes, providerType);
	}

	@Override
	public String getId() {
		return super.getAttributes().get("id").toString();
	}

	@Override
	public String getLoginId() {
		return null;
	}

	@Override
	public String getName() {
		Map<String, Object> properties = (Map<String, Object>) super.getAttributes().get("properties");

		if (properties == null) {
			return null;
		}

		return (String) properties.get("nickname");
	}

	@Override
	public String getEmail() {
		return (String) super.getAttributes().get("account_email");
	}

	@Override
	public String getImageUrl() {
		Map<String, Object> properties = (Map<String, Object>) super.getAttributes().get("properties");

		if (properties == null) {
			return null;
		}

		return (String) properties.get("thumbnail_image");
	}

	@Override
	public String getAccessToken() {
		return null;
	}
}
