package com.shk95.giteditor.domain.common.security.info.impl;


import com.shk95.giteditor.domain.common.security.info.OAuth2UserInfo;

import java.util.Map;

@Deprecated
public class NaverOAuth2UserInfo extends OAuth2UserInfo {

	public NaverOAuth2UserInfo(Map<String, Object> attributes, Map<String, String> additionalAttributes) {
		super(attributes, additionalAttributes);
	}

	@Override
	public String getId() {
		Map<String, Object> response = (Map<String, Object>) attributes.get("response");

		if (response == null) {
			return null;
		}

		return (String) response.get("id");
	}

	@Override
	public String getName() {
		Map<String, Object> response = (Map<String, Object>) attributes.get("response");

		if (response == null) {
			return null;
		}

		return (String) response.get("nickname");
	}

	@Override
	public String getEmail() {
		Map<String, Object> response = (Map<String, Object>) attributes.get("response");

		if (response == null) {
			return null;
		}

		return (String) response.get("email");
	}

	@Override
	public String getImageUrl() {
		Map<String, Object> response = (Map<String, Object>) attributes.get("response");

		if (response == null) {
			return null;
		}

		return (String) response.get("profile_image");
	}
}
