package com.shk95.giteditor.domain.common.security.info;

import java.util.Map;

public abstract class OAuth2UserInfo {
	protected Map<String, Object> attributes;
	protected Map<String, String> additionalAttributes;

	public OAuth2UserInfo(Map<String, Object> attributes, Map<String, String> additionalAttributes) {
		this.attributes = attributes;
		this.additionalAttributes = additionalAttributes;
	}

	public Map<String, String> getAdditionalAttributes() {
		return additionalAttributes;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public abstract String getId();

	public abstract String getName();

	public abstract String getEmail();

	public abstract String getImageUrl();
}
