package com.shk95.giteditor.domain.model.user.oauth;


import com.shk95.giteditor.domain.common.constant.ProviderType;
import com.shk95.giteditor.domain.common.model.AbstractOAuth2UserInfo;

import java.util.Map;

import static com.shk95.giteditor.config.ConstantFields.OAuthService.PROVIDER_ACCESS_TOKEN;

public class KakaoOAuth2UserInfo extends AbstractOAuth2UserInfo {

	public KakaoOAuth2UserInfo(Map<String, Object> attributes, Map<String, String> additionalAttributes, ProviderType providerType) {
		super(attributes, additionalAttributes, providerType);
	}

	@Override
	public String getId() {
		return super.getAttributes().get("id").toString();
	}

	@Override
	public String getLoginId() {
		return super.getAttributes().get("id").toString();
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
		String email = (String) super.getAttributes().get("account_email");
		return email != null ? email : (String) ((Map<?, ?>) super.getAttributes().get("kakao_account")).get("email");
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
		return super.getAdditionalAttributes().get(PROVIDER_ACCESS_TOKEN);
	}
}
