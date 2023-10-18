package com.shk95.giteditor.core.user.application.port.out.projection;

import com.shk95.giteditor.common.constant.ProviderType;

public interface UserIdProjection {

	String getUserLoginId();

	ProviderType getProviderType();
}
