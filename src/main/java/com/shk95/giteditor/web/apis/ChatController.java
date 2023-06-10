package com.shk95.giteditor.web.apis;

import com.shk95.giteditor.domain.application.ChatService;
import com.shk95.giteditor.domain.application.commands.chat.GetCompletionCommand;
import com.shk95.giteditor.domain.application.commands.chat.RequestCommand;
import com.shk95.giteditor.domain.common.security.CurrentUser;
import com.shk95.giteditor.domain.common.security.UserAuthorize;
import com.shk95.giteditor.domain.model.user.CustomUserDetails;
import com.shk95.giteditor.utils.Response;
import com.shk95.giteditor.web.apis.request.ChatRequest;
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

	private final ChatService chatService;

	@PostMapping
	public ResponseEntity<?> simpleResponse(@CurrentUser CustomUserDetails userDetails,
											@Validated @RequestBody ChatRequest.Simple request) {
		return userDetails.isOpenAIEnabled()
			? Response.success(
			chatService.simpleRequest(RequestCommand.builder()
				.userId(userDetails.getUserEntityId())
				.prompt(request.getPrompt())
				.accessToken(userDetails.getOpenAIAccessToken()).build())
			, "Get simple ChatGpt Response", HttpStatus.OK)
			: Response.fail("Chat Service 를 사용할 수 없습니다.", HttpStatus.FORBIDDEN);
	}

	@GetMapping
	public ResponseEntity<?> getCompletions(@CurrentUser CustomUserDetails userDetails,
											@Validated @RequestBody ChatRequest.Completion request) {
		return userDetails.isOpenAIEnabled()
			? Response.success(
			chatService.getCompletions(GetCompletionCommand.builder()
				.userId(userDetails.getUserEntityId())
				.accessToken(userDetails.getOpenAIAccessToken())
				.pageAt(request.getPageAt())
				.size(request.getSize())
				.build())
			, "메시지를 가져왔습니다", HttpStatus.OK)
			: Response.fail("Chat Service 를 사용할 수 없습니다.", HttpStatus.FORBIDDEN);
	}
}
