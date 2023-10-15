package com.shk95.giteditor.core.openai.adapter.out;

import com.shk95.giteditor.core.openai.application.port.out.GptApiPort;
import com.shk95.giteditor.core.openai.infrastructure.SimpleGpt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GptApiAdapter implements GptApiPort {

	private final SimpleGpt simpleGpt;

	@Override
	public String getCompletion(String apiKey, String prompt) {
		return simpleGpt.getCompletion(apiKey, prompt);
	}

	@Override
	public boolean isAvailable(String apiKey) {
		return simpleGpt.isAvailable(apiKey);
	}
}
