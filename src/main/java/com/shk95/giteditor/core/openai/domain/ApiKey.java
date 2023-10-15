package com.shk95.giteditor.core.openai.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

@Setter
@Getter
public class ApiKey {

	private String apiKey;

	public ApiKey(String apiKey) {
		Assert.hasText(apiKey, "ApiKey Cannot be null.");
		this.apiKey = apiKey;
	}
}
