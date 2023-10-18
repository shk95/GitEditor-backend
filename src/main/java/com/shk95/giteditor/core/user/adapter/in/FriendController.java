package com.shk95.giteditor.core.user.adapter.in;

import com.shk95.giteditor.common.security.CurrentUser;
import com.shk95.giteditor.common.utils.Response;
import com.shk95.giteditor.core.auth.domain.CustomUserDetails;
import com.shk95.giteditor.core.user.application.port.in.AcceptFriendUseCase;
import com.shk95.giteditor.core.user.application.port.in.AddFriendUseCase;
import com.shk95.giteditor.core.user.application.port.in.GetFriendUseCase;
import com.shk95.giteditor.core.user.application.service.dto.FriendDto;
import com.shk95.giteditor.core.user.domain.friend.FriendshipStatus;
import com.shk95.giteditor.core.user.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user/friend")
@RestController
public class FriendController {

	private final AddFriendUseCase addFriendUseCase;
	private final GetFriendUseCase getFriendUseCase;
	private final AcceptFriendUseCase acceptFriendUseCase;

	// 본인은 ACCEPTED, 상대방에게는 PENDING 처리
	@PostMapping("/request")
	public ResponseEntity<?> sendFriendRequest(@CurrentUser CustomUserDetails userDetails,
	                                           @RequestBody FriendRequest request) {
		UserId myId = userDetails.getUserId();
		UserId addresseeUserId = UserId.of(request.getAddresseeId());
		addFriendUseCase.request(myId, addresseeUserId);
		return Response.success();
	}

	// 본인의 PENDING -> ACCEPTED
	@PostMapping("/accept")
	public ResponseEntity<?> acceptFriendRequest(@CurrentUser CustomUserDetails userDetails,
	                                             @RequestBody FriendRequest request) {
		UserId myId = userDetails.getUserId();
		UserId addresseeUserId = UserId.of(request.getAddresseeId());
		acceptFriendUseCase.acceptRequest(myId, addresseeUserId);
		return Response.success();
	}

	@GetMapping("/{status}")
	public ResponseEntity<?> getAllFriendsByStatus(@CurrentUser CustomUserDetails userDetails,
	                                               @PathVariable FriendshipStatus status) {
		UserId myId = userDetails.getUserId();
		List<FriendDto> list = getFriendUseCase.getFriendsByStatus(myId, status);
		return Response.success(list);
	}

	@GetMapping("/all")
	public ResponseEntity<?> getAllFriends(@CurrentUser CustomUserDetails userDetails) {
		UserId myId = userDetails.getUserId();
		List<FriendDto> list = getFriendUseCase.getFriendsByStatus(myId, null);
		return Response.success(list);
	}
}
