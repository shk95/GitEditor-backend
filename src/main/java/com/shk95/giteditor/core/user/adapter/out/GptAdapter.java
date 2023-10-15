package com.shk95.giteditor.core.user.adapter.out;

import com.shk95.giteditor.core.openai.application.port.out.GptApiPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GptAdapter {

	private final GptApiPort gptService;

	public boolean isAvailable(String apkKey) {
		return gptService.isAvailable(apkKey);
	}
}
