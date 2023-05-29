package com.shk95.giteditor.domain.model.user.oauth;


import com.shk95.giteditor.domain.common.constant.ProviderType;
import com.shk95.giteditor.domain.common.model.AbstractOAuth2UserInfo;

import java.util.Map;

import static com.shk95.giteditor.config.ConstantFields.OAuthService.PROVIDER_ACCESS_TOKEN;

public class GoogleOAuth2UserInfo extends AbstractOAuth2UserInfo {

	public GoogleOAuth2UserInfo(Map<String, Object> attributes, Map<String, String> additionalAttributes, ProviderType providerType) {
		super(attributes, additionalAttributes, providerType);
	}

	@Override
	public String getId() {
		return (String) super.getAttributes().get("sub");
	}

	@Override
	public String getLoginId() {
		return (String) super.getAttributes().get("sub");
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
		return super.getAdditionalAttributes().get(PROVIDER_ACCESS_TOKEN);
	}
}
