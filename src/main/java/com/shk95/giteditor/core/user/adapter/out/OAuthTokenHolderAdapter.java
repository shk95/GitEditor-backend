package com.shk95.giteditor.core.user.adapter.out;

import com.shk95.giteditor.core.user.application.port.out.OAuthTokenHolderPort;
import com.shk95.giteditor.core.user.domain.provider.ProviderLoginInfo;
import com.shk95.giteditor.core.user.infrastructure.RedisOAuthTokenHolderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class OAuthTokenHolderAdapter implements OAuthTokenHolderPort {

	private final RedisOAuthTokenHolderRepository redisOAuthTokenHolderRepository;

	@Override
	public ProviderLoginInfo save(ProviderLoginInfo loginInfo) {
		return redisOAuthTokenHolderRepository.save(loginInfo);
	}

	@Override
	public Optional<ProviderLoginInfo> findById(String id) {
		return redisOAuthTokenHolderRepository.findById(id);
	}

	@Override
	public boolean existsById(String id) {
		return redisOAuthTokenHolderRepository.existsById(id);
	}

	@Override
	public void deleteById(String id) {
		redisOAuthTokenHolderRepository.deleteById(id);
	}
}
