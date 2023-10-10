package com.shk95.giteditor.core.user.application.port.out;

import com.shk95.giteditor.core.user.domain.provider.ProviderLoginInfo;

import java.util.Optional;

public interface OAuthTokenHolderPort {

	ProviderLoginInfo save(ProviderLoginInfo loginInfo);

	Optional<ProviderLoginInfo> findById(String id);

	boolean existsById(String id);

	void deleteById(String id);
}
