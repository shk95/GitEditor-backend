package com.shk95.giteditor.core.user.application.port.out;

import com.shk95.giteditor.core.user.domain.friend.Friend;
import com.shk95.giteditor.core.user.domain.friend.FriendshipStatus;
import com.shk95.giteditor.core.user.domain.user.User;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.Optional;

public interface FriendRepositoryPort {

	void acceptFriend(Friend friend);

	void requestFriend(Friend friend);

	boolean isFriend(User requester, User Addressee); // 이미 친구목록이 만들어졌는지

	Optional<Friend> findByRequestAndAddressee(User requester, User addressee);

	List<Friend> findFriendsByUserAndStatus(User user, @Nullable FriendshipStatus status);
}
