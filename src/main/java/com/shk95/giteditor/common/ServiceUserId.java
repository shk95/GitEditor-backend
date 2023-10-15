package com.shk95.giteditor.common;

import com.shk95.giteditor.core.auth.domain.CustomUserDetails;
import com.shk95.giteditor.core.user.domain.user.UserId;

public class ServiceUserId {

	private final String id;
	private final UserId userId;

	private ServiceUserId(UserId userid) {
		this.id = userid.get();
		this.userId = userid;
	}

	public static ServiceUserId from(CustomUserDetails userDetails) {
		return new ServiceUserId(userDetails.getUserId());
	}

	public static ServiceUserId from(String providerTypeAndLoginId) {
		return new ServiceUserId(UserId.of(providerTypeAndLoginId));
	}

	public static ServiceUserId from(UserId userId) {
		return new ServiceUserId(userId);
	}

	public String get() {
		return this.id;
	}

	public UserId userId() {
		return this.userId;
	}
}
