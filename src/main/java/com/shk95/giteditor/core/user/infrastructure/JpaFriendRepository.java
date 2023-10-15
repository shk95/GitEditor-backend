package com.shk95.giteditor.core.user.infrastructure;

import com.shk95.giteditor.core.user.domain.friend.Friend;
import com.shk95.giteditor.core.user.domain.friend.FriendshipStatus;
import com.shk95.giteditor.core.user.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaFriendRepository extends JpaRepository<Friend, Long> {

	Optional<Friend> findByRequesterAndAddressee(User requester, User Addressee);

	List<Friend> findByRequester(User requester);

	List<Friend> findByRequesterAndStatus(User requester, FriendshipStatus status);

}
