package com.shk95.giteditor.core.user.adapter.out;

import com.shk95.giteditor.core.user.application.port.out.FriendRepositoryPort;
import com.shk95.giteditor.core.user.domain.friend.Friend;
import com.shk95.giteditor.core.user.domain.friend.FriendshipStatus;
import com.shk95.giteditor.core.user.domain.user.User;
import com.shk95.giteditor.core.user.infrastructure.JpaFriendRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class FriendRepositoryAdapter implements FriendRepositoryPort {

	private final JpaFriendRepository jpaFriendRepository;

	@Override
	public void acceptFriend(Friend friend) {
		friend.setStatus(FriendshipStatus.ACCEPTED);
		jpaFriendRepository.save(friend);
	}

	@Override
	public void requestFriend(Friend friend) {
		friend.setStatus(FriendshipStatus.PENDING);
		jpaFriendRepository.save(friend);
	}

	@Override
	public boolean isFriend(User requester, User addressee) {
		return jpaFriendRepository.findByRequesterAndAddressee(requester, addressee)
			.isPresent();
	}

	@Override
	public Optional<Friend> findByRequestAndAddressee(User requester, User addressee) {
		return jpaFriendRepository.findByRequesterAndAddressee(requester, addressee);
	}

	@Override
	public List<Friend> findFriendsByUserAndStatus(User user, @Nullable FriendshipStatus status) {
		return status == null ?
			jpaFriendRepository.findByRequester(user) :
			jpaFriendRepository.findByRequesterAndStatus(user, status);
	}

}
