package com.shk95.giteditor.core.openai.infrastructure;

import io.github.aminovmaksim.chatgpt4j.ChatGPTClient;
import io.github.aminovmaksim.chatgpt4j.model.ChatRequest;
import io.github.aminovmaksim.chatgpt4j.model.enums.ModelType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SimpleGpt {

	public String getCompletion(String apiKey, String prompt) {
		log.info("Simple Gpt has requested");
		ChatRequest request = new ChatRequest(prompt);
		request.setModel(ModelType.GPT_3_5_TURBO.getName());
		return ChatGPTClient.builder()
			.apiKey(apiKey)
			.requestTimeout(50000L) // optional, default is 60000 ms
			.baseUrl("https://api.openai.com/v1")
			.build().sendChat(request).getChoices().get(0).getMessage().getContent();
	}

	public boolean isAvailable(String apiKey) {
		try {
			this.getCompletion(apiKey, "just say hi");
		} catch (Exception e) {
			log.info("Failed to verify OpenAI access token");
			return false;
		}
		return true;
	}

}
