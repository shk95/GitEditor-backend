package com.shk95.giteditor.core.user.application.service.dto;

import com.shk95.giteditor.common.constant.ProviderType;
import com.shk95.giteditor.core.user.domain.friend.FriendshipStatus;

public record FriendDto(
	ProviderType addresseeUserIdProviderType,
	String addresseeUserIdUserLoginId,
	String addresseeDefaultEmail,
	String addresseeUsername,
	FriendshipStatus status
) {

}
