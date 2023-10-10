package com.shk95.giteditor.common.model;

import com.shk95.giteditor.common.constant.ProviderType;

import java.util.Map;

public abstract class AbstractOAuth2UserInfo {
	private final Map<String, Object> attributes;
	private final Map<String, String> additionalAttributes;
	private final ProviderType providerType;

	public AbstractOAuth2UserInfo(Map<String, Object> attributes, Map<String, String> additionalAttributes, ProviderType providerType) {
		this.attributes = attributes;
		this.additionalAttributes = additionalAttributes;
		this.providerType = providerType;
	}

	public Map<String, String> getAdditionalAttributes() {
		return this.additionalAttributes;
	}

	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	public ProviderType getProviderType() {
		return this.providerType;
	}

	/**
	 * @return must be unique value
	 * 서비스에서 제공하는 유일한 변하지않는 값.
	 */
	public abstract String getId();


	public abstract String getLoginId();

	public abstract String getName();

	public abstract String getEmail();

	public abstract String getImageUrl();

	public abstract String getAccessToken();
}
