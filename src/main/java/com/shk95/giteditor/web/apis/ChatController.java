package com.shk95.giteditor.web.apis;

import com.shk95.giteditor.domain.application.ChatService;
import com.shk95.giteditor.domain.application.commands.chat.GetCompletionCommand;
import com.shk95.giteditor.domain.application.commands.chat.RequestCommand;
import com.shk95.giteditor.domain.common.security.CurrentUser;
import com.shk95.giteditor.domain.common.security.UserAuthorize;
import com.shk95.giteditor.domain.model.chat.Chat;
import com.shk95.giteditor.domain.model.user.CustomUserDetails;
import com.shk95.giteditor.utils.Response;
import com.shk95.giteditor.web.apis.request.ChatRequest;
import com.shk95.giteditor.web.apis.response.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

	private final ChatService chatService;

	@PostMapping
	public ResponseEntity<?> simpleResponse(@CurrentUser CustomUserDetails userDetails,
											@Validated @RequestBody ChatRequest.Simple request) {
		Chat chat = chatService.simpleRequest(RequestCommand.builder()
			.userId(userDetails.getUserEntityId())
			.prompt(request.getPrompt())
			.accessToken(userDetails.getOpenAIAccessToken()).build());
		return userDetails.isOpenAIEnabled()
			? Response.success(ChatResponse.Message.builder()
				.prompt(chat.getPrompt())
				.completion(chat.getCompletion())
				.createdDate(chat.getCreatedDate()).build()
			, "Get simple ChatGpt Response", HttpStatus.OK)
			: Response.fail("Chat Service 를 사용할 수 없습니다.", HttpStatus.FORBIDDEN);
	}

	@GetMapping
	public ResponseEntity<?> getCompletions(@CurrentUser CustomUserDetails userDetails,
											@RequestParam String pageAt, @RequestParam String pageSize) {
		Page<Chat> messages = chatService.getCompletions(GetCompletionCommand.builder()
			.userId(userDetails.getUserEntityId())
			.accessToken(userDetails.getOpenAIAccessToken())
			.pageAt(Integer.parseInt(pageAt))
			.size(Integer.parseInt(pageSize))
			.build());
		return userDetails.isOpenAIEnabled()
			? Response.success(ChatResponse.Messages.of(messages)
			, "메시지를 가져왔습니다", HttpStatus.OK)
			: Response.fail("Chat Service 를 사용할 수 없습니다.", HttpStatus.FORBIDDEN);
	}
}
