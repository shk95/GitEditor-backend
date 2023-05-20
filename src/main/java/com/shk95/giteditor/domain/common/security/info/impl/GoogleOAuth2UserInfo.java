package com.shk95.giteditor.domain.common.security.info.impl;


import com.shk95.giteditor.domain.common.constant.ProviderType;
import com.shk95.giteditor.domain.common.security.info.OAuth2UserInfo;

import java.util.Map;

@Deprecated
public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

	public GoogleOAuth2UserInfo(Map<String, Object> attributes, Map<String, String> additionalAttributes, ProviderType providerType) {
		super(attributes, additionalAttributes, providerType);
	}

	@Override
	public String getId() {
		return (String) super.getAttributes().get("sub");
	}

	@Override
	public String getLoginId() {
		return null;
	}

	@Override
	public String getName() {
		return (String) super.getAttributes().get("name");
	}

	@Override
	public String getEmail() {
		return (String) super.getAttributes().get("email");
	}

	@Override
	public String getImageUrl() {
		return (String) super.getAttributes().get("picture");
	}

	@Override
	public String getAccessToken() {
		return null;
	}
}
