package com.shk95.giteditor.core.user.application.service;

import com.shk95.giteditor.core.user.application.port.in.AcceptFriendUseCase;
import com.shk95.giteditor.core.user.application.port.in.AddFriendUseCase;
import com.shk95.giteditor.core.user.application.port.in.GetFriendUseCase;
import com.shk95.giteditor.core.user.application.port.out.FriendRepositoryPort;
import com.shk95.giteditor.core.user.application.port.out.UserCrudRepositoryPort;
import com.shk95.giteditor.core.user.application.service.dto.FriendDto;
import com.shk95.giteditor.core.user.domain.friend.Friend;
import com.shk95.giteditor.core.user.domain.friend.FriendshipStatus;
import com.shk95.giteditor.core.user.domain.user.User;
import com.shk95.giteditor.core.user.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class FriendService implements AddFriendUseCase, AcceptFriendUseCase, GetFriendUseCase {

	private final FriendRepositoryPort friendRepositoryPort;
	private final UserCrudRepositoryPort userCrudRepositoryPort;

	@Transactional
	@Override
	public void request(UserId myId, UserId addresseeId) {
		Optional<User> me = userCrudRepositoryPort.findUserByUserId(myId);
		Optional<User> addresseeUser = userCrudRepositoryPort.findUserByUserId(addresseeId);
		if (me.isEmpty() || addresseeUser.isEmpty()) {
			log.info("친구목록 추가 실패.");
			return;
		}

		// 이미 추가되어있는지 검증
		if (friendRepositoryPort.isFriend(me.get(), addresseeUser.get())) {
			log.info("이미 추가되어있는 친구.");
			return;
		}

		Friend request = new Friend();
		request.setRequester(me.get());
		request.setAddressee(addresseeUser.get());
		friendRepositoryPort.acceptFriend(request);

		Friend addresseeFriend = new Friend();
		addresseeFriend.setRequester(addresseeUser.get());
		addresseeFriend.setAddressee(me.get());
		friendRepositoryPort.requestFriend(addresseeFriend);
	}

	@Transactional
	@Override
	public void acceptRequest(UserId myId, UserId addressee) {
		Optional<User> me = userCrudRepositoryPort.findUserByUserId(myId);
		Optional<User> addresseeUser = userCrudRepositoryPort.findUserByUserId(addressee);
		if (me.isEmpty() || addresseeUser.isEmpty()) {
			log.info("친구목록 추가 실패.");
			return;
		}
		final Optional<Friend> friend = friendRepositoryPort.findByRequestAndAddressee(me.get(), addresseeUser.get());
		friend.ifPresent(f -> friendRepositoryPort.acceptFriend(friend.get()));
	}

	@Transactional(readOnly = true)
	@Override
	public List<FriendDto> getFriendsByStatus(UserId myId, FriendshipStatus status) {
		return userCrudRepositoryPort.findUserByUserId(myId)
			.map(user -> friendRepositoryPort.findFriendsByUserAndStatus(user, status))
			.map(friends ->
				friends.stream()
					.map(friend ->
						new FriendDto(friend.getAddressee().getUserId().getProviderType(),
							friend.getAddressee().getUserId().getUserLoginId(),
							friend.getAddressee().getProfileImageUrl(),
							friend.getAddressee().getDefaultEmail(),
							friend.getAddressee().getUsername(),
							friend.getStatus())).collect(Collectors.toList())
			).orElseGet(ArrayList::new);
	}
}
