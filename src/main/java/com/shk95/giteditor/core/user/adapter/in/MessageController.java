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
@RequestMapping("/user/messages")
@RestController
public class MessageController {

	private final GetMessageUseCase getMessageUseCase;
	private final SendMessageUseCase sendMessageUseCase;

	@GetMapping("/unread/count")
	public ResponseEntity<?> getUnreadCount(@CurrentUser CustomUserDetails userDetails) {
		int count = getMessageUseCase.getUnreadCount(userDetails.getUserId());
		return Response.success(count);
	}

	@GetMapping("/unread")
	public ResponseEntity<?> getUnreadMessages(@CurrentUser CustomUserDetails userDetails) {
		List<MessageDto> messages = getMessageUseCase.getUnreadMessages(userDetails.getUserId());
		return Response.success(messages);
	}

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
		// 메시지는 가져오면서 읽음으로 처리
		List<MessageDto> conversation =
			getMessageUseCase
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
