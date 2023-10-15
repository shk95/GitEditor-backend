package com.shk95.giteditor.core.user.application.port.in;

import com.shk95.giteditor.core.user.domain.user.UserId;

public interface AddFriendUseCase {

	void request(UserId myId, UserId addressee);
}
