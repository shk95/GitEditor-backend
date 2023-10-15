package com.shk95.giteditor.core.openai.application.port.out;

import com.shk95.giteditor.core.openai.adapter.out.GptApiAdapter;
import com.shk95.giteditor.core.openai.infrastructure.SimpleGpt;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
@ActiveProfiles(profiles = {"dev", "test"})
class GptApiPortTest {

	@Value("${test.gpt-api-key}")
	public String apiKey;

	@Autowired
	private GptApiAdapter gptApiAdapter;

	@Mock
	private SimpleGpt simpleGptMock;

	@InjectMocks
	private GptApiAdapter gptApiAdapterMock;

	@Test
	void getCompletion_shouldCallSimpleGpt_andReturnExpectedCompletion() {
		// Given
		String apiKey = "testApiKey";
		String prompt = "testPrompt";
		String expectedCompletion = "testCompletion";

		// Mock
		when(simpleGptMock.getCompletion(apiKey, prompt)).thenReturn(expectedCompletion);

		// When
		String completion = gptApiAdapterMock.getCompletion(apiKey, prompt);

		// Then
		verify(simpleGptMock, times(1)).getCompletion(apiKey, prompt);
		assertTrue(completion.equals(expectedCompletion));
	}

	@Test
	void isAvailable_shouldReturnTrue_whenSimpleGptDoesNotThrowException() { // Mock Api
		// Given
		String apiKey = "testApiKey";
		when(simpleGptMock.isAvailable(apiKey)).thenReturn(true);

		// When
		boolean isAvailable = gptApiAdapterMock.isAvailable(apiKey);

		// Then
		verify(simpleGptMock, times(1)).isAvailable(apiKey);
		assertTrue(isAvailable);
	}

	@Test
	void isAvailable_shouldReturnFalse_whenSimpleGptThrowsException() { // Mock Api
		// Given
		final String apiKey = "wrong-api-key";

		// Mock
		doThrow(RuntimeException.class).when(simpleGptMock).getCompletion(eq(apiKey), anyString());

		// When
		boolean isAvailable = gptApiAdapterMock.isAvailable(apiKey);

		// Then
		verify(simpleGptMock, times(1)).getCompletion(eq(apiKey), anyString());
		assertFalse(isAvailable);
	}

	@Test
	void isAvailable_shouldReturnFalse_whenApiKeyIsInvalid() {// Real Api
		// Given
		final String invalidApiKey = "invalid-api-key";

		// When
		boolean isAvailable = gptApiAdapter.isAvailable(invalidApiKey);

		// Then
		assertFalse(isAvailable);
	}

	@Test
	void isAvailable_shouldReturnTrue_whenApiKeyIsValid() { // Real Api
		// Given
		final String validApiKey = apiKey; // Replace with your actual API key
		System.out.println("__________________________");
		System.out.println("openai valid api key : " + validApiKey);

		// When
		boolean isAvailable = gptApiAdapter.isAvailable(validApiKey);

		// Then
		assertTrue(isAvailable);
	}
}
