package com.shk95.giteditor.core.openai.application.port.in;

import com.shk95.giteditor.core.openai.domain.ChatDocument;
import com.shk95.giteditor.core.openai.application.port.in.command.RequestCommand;
import com.shk95.giteditor.core.openai.application.port.out.GptApiPort;
import com.shk95.giteditor.core.openai.application.port.out.SaveMessagePort;
import com.shk95.giteditor.core.openai.application.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.yml")
class CreateCompletionUseCaseTest {

	@Mock
	private GptApiPort gptService;

	@Mock
	private SaveMessagePort saveMessagePort;

	@InjectMocks
	private ChatService chatService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void createCompletion_shouldCallDependencies_andReturnChatDocument() {
		// Given
		RequestCommand command = RequestCommand.builder()
			.userId("user123")
			.accessToken("accessToken")
			.prompt("prompt")
			.build();

		String prompt = command.prompt();
		String completion = "completion from GPT-3";
		ChatDocument expectedChatDocument = ChatDocument.builder()
			.userId(command.userId())
			.prompt(prompt)
			.completion(completion)
			.build();

		// Mock
		when(gptService.getCompletion(eq(command.accessToken()), eq(command.prompt()))).thenReturn(completion);
		when(saveMessagePort.save(eq(command.userId()), eq(prompt), eq(completion))).thenReturn(expectedChatDocument);

		// When
		ChatDocument resultChatDocument = chatService.createCompletion(command);
		System.out.println("result document returned : " + resultChatDocument.toString());

		// Then
		verify(gptService, times(1)).getCompletion(command.accessToken(), command.prompt());
		verify(saveMessagePort, times(1)).save(command.userId(), prompt, completion);
	}
}
