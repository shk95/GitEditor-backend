package com.shk95.giteditor.domain.model.github;

import com.shk95.giteditor.domain.common.constant.ProviderType;
import com.shk95.giteditor.domain.model.user.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ServiceUserInfo {
	private final String serviceUserId;
	private final ProviderType providerType;

	public static ServiceUserInfo userId(UserId userId) {
		return new ServiceUserInfo(userId.getUserLoginId(), userId.getProviderType());
	}

	public UserId getUserId() {
		return new UserId(providerType, serviceUserId);
	}
}
