package com.shk95.giteditor.core.user.adapter.in;

import com.shk95.giteditor.common.security.CurrentUser;
import com.shk95.giteditor.common.utils.Response;
import com.shk95.giteditor.core.auth.domain.CustomUserDetails;
import com.shk95.giteditor.core.user.application.port.in.GetMessageUseCase;
import com.shk95.giteditor.core.user.application.port.in.SendMessageUseCase;
import com.shk95.giteditor.core.user.application.service.dto.MessageDto;
import com.shk95.giteditor.core.user.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/messages")
@RestController
public class MessageController {

	private final GetMessageUseCase getMessageUseCase;
	private final SendMessageUseCase sendMessageUseCase;

	@PostMapping("/send")
	public ResponseEntity<?> sendMessage(@CurrentUser CustomUserDetails userDetails,
	                                     @RequestBody MessageDto messageDto) {
		sendMessageUseCase.sendMessage(
			userDetails.getUserId(),
			UserId.of(messageDto.recipientUserIdProviderType().name() + "," + messageDto.recipientUserIdUserLoginId()),
			messageDto.content());
		return Response.success("Message sent successfully");
	}

	@GetMapping("/conversation")
	public ResponseEntity<?> getConversation(@CurrentUser CustomUserDetails userDetails,
	                                         @RequestParam String recipientId) {
		List<MessageDto> conversation = getMessageUseCase
			.getMessagesFrom(
				userDetails.getUserId(),
				UserId.of(recipientId));
		return Response.success(conversation);
	}

	@GetMapping("/all") // M:N 관계로 보완
	public ResponseEntity<?> getAllConversation(@CurrentUser CustomUserDetails userDetails) {
		List<MessageDto> conversation = getMessageUseCase
			.getAllMyMessages(userDetails.getUserId());
		return Response.success(conversation);
	}
}
