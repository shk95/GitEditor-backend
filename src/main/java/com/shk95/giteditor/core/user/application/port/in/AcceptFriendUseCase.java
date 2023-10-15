package com.shk95.giteditor.core.user.application.port.in;

import com.shk95.giteditor.core.user.domain.user.UserId;

public interface AcceptFriendUseCase {

	void acceptRequest(UserId myId, UserId addressee);
}
