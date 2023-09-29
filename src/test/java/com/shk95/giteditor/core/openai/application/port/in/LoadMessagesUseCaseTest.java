package com.shk95.giteditor.core.openai.application.port.in;

import com.shk95.giteditor.core.openai.application.port.out.LoadMessagePort;
import com.shk95.giteditor.core.openai.application.service.ChatService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LoadMessagesUseCaseTest {

	@Mock
	private LoadMessagePort loadChatPort;

	@InjectMocks
	private ChatService chatService;
}
