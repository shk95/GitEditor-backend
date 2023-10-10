package com.shk95.giteditor.core.openai.adapter.in;

import com.shk95.giteditor.common.ServiceUserId;
import com.shk95.giteditor.common.security.CurrentUser;
import com.shk95.giteditor.common.security.UserAuthorize;
import com.shk95.giteditor.common.utils.Response;
import com.shk95.giteditor.core.auth.domain.CustomUserDetails;
import com.shk95.giteditor.core.openai.application.port.in.CreateCompletionUseCase;
import com.shk95.giteditor.core.openai.application.port.in.LoadMessagesUseCase;
import com.shk95.giteditor.core.openai.application.port.in.command.GetCompletionCommand;
import com.shk95.giteditor.core.openai.application.port.in.command.RequestCommand;
import com.shk95.giteditor.core.openai.application.service.PageResult;
import com.shk95.giteditor.core.openai.domain.ChatDocument;
import com.shk95.giteditor.core.user.application.port.in.FetchUserInfoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/chat")
@UserAuthorize
@RequiredArgsConstructor
@RestController
public class ChatController {

	private final LoadMessagesUseCase loadCompletionUseCase;
	private final CreateCompletionUseCase createCompletionUseCase;
	private final FetchUserInfoUseCase fetchUserInfoUseCase;

	@PostMapping
	public ResponseEntity<?> createCompletion(@CurrentUser CustomUserDetails userDetails,
	                                          @Validated @RequestBody ChatRequest.Simple request) {
		if (!userDetails.isOpenAIEnabled()) {
			return Response.fail("Chat Service 를 사용할 수 없습니다.", HttpStatus.FORBIDDEN);
		}
		ChatDocument chatDocument = createCompletionUseCase.createCompletion(
			RequestCommand.builder()
				.userId(ServiceUserId.from(userDetails.getProviderTypeAndLoginId()).get())
				.prompt(request.getPrompt())
				.accessToken(fetchUserInfoUseCase.fetchOpenAIAccessToken(userDetails.getUserId())).build());
		return Response.success(ChatResponse.Message.builder()
				.prompt(chatDocument.getPrompt())
				.completion(chatDocument.getCompletion())
				.createdDate(chatDocument.getCreatedDate()).build(),
			"Get simple ChatGpt Response", HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<?> loadMessages(@CurrentUser CustomUserDetails userDetails,
	                                      @RequestParam String pageAt, @RequestParam String pageSize) {
		if (!userDetails.isOpenAIEnabled()) {
			return Response.fail("Chat Service 를 사용할 수 없습니다.", HttpStatus.FORBIDDEN);
		}
		PageResult<ChatDocument> messages = loadCompletionUseCase.loadMessages(
			GetCompletionCommand.builder()
				.userId(ServiceUserId.from(userDetails.getProviderTypeAndLoginId()).get())
				.accessToken(fetchUserInfoUseCase.fetchOpenAIAccessToken(userDetails.getUserId()))
				.pageAt(Integer.parseInt(pageAt))
				.size(Integer.parseInt(pageSize))
				.build());
		return Response.success(ChatResponse.Messages.of(messages), "메시지를 가져왔습니다", HttpStatus.OK);
	}
}
