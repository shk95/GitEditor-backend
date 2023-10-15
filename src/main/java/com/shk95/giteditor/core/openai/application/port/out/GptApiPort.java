package com.shk95.giteditor.core.openai.application.port.out;

public interface GptApiPort {

	String getCompletion(String apiKey, String prompt);

	boolean isAvailable(String apiKey);
}
