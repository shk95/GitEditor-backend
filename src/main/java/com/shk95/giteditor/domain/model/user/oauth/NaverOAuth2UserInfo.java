package com.shk95.giteditor.domain.model.user.oauth;


import com.shk95.giteditor.domain.common.constant.ProviderType;
import com.shk95.giteditor.domain.common.model.AbstractOAuth2UserInfo;

import java.util.Map;

import static com.shk95.giteditor.config.ConstantFields.OAuthService.PROVIDER_ACCESS_TOKEN;

public class NaverOAuth2UserInfo extends AbstractOAuth2UserInfo {

	public NaverOAuth2UserInfo(Map<String, Object> attributes, Map<String, String> additionalAttributes, ProviderType providerType) {
		super(attributes, additionalAttributes, providerType);
	}

	@Override
	public String getId() {
		Map<String, Object> response = (Map<String, Object>) super.getAttributes().get("response");

		if (response == null) {
			return null;
		}

		return (String) response.get("id");
	}

	@Override
	public String getLoginId() {
		return this.getId();
	}

	@Override
	public String getName() {
		Map<String, Object> response = (Map<String, Object>) super.getAttributes().get("response");

		if (response == null) {
			return null;
		}

		return (String) response.get("nickname");
	}

	@Override
	public String getEmail() {
		Map<String, Object> response = (Map<String, Object>) super.getAttributes().get("response");

		if (response == null) {
			return null;
		}

		return (String) response.get("email");
	}

	@Override
	public String getImageUrl() {
		Map<String, Object> response = (Map<String, Object>) super.getAttributes().get("response");

		if (response == null) {
			return null;
		}

		return (String) response.get("profile_image");
	}

	@Override
	public String getAccessToken() {
		return super.getAdditionalAttributes().get(PROVIDER_ACCESS_TOKEN);
	}
}
