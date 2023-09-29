package com.shk95.giteditor.core.openai.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.client.AiClient;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SpringAi {

	private final AiClient aiClient;

	public String completion(String prompt) {
		return aiClient.generate(prompt);
	}

}
