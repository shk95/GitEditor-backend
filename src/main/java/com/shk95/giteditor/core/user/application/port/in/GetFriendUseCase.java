package com.shk95.giteditor.core.user.application.port.in;

import com.shk95.giteditor.core.user.application.service.dto.FriendDto;
import com.shk95.giteditor.core.user.domain.friend.FriendshipStatus;
import com.shk95.giteditor.core.user.domain.user.UserId;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface GetFriendUseCase {

	List<FriendDto> getFriendsByStatus(UserId myId, @Nullable FriendshipStatus status);
}
