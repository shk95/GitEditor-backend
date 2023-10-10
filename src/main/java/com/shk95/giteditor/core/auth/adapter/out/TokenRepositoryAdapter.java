package com.shk95.giteditor.core.auth.adapter.out;

import com.shk95.giteditor.core.auth.application.port.out.BlacklistTokenRepositoryPort;
import com.shk95.giteditor.core.auth.application.port.out.RefreshTokenRepositoryPort;
import com.shk95.giteditor.core.auth.domain.BlacklistToken;
import com.shk95.giteditor.core.auth.domain.RefreshToken;
import com.shk95.giteditor.core.auth.infrastructure.RedisBlacklistTokenRepository;
import com.shk95.giteditor.core.auth.infrastructure.RedisRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class TokenRepositoryAdapter implements BlacklistTokenRepositoryPort, RefreshTokenRepositoryPort {

	private final RedisBlacklistTokenRepository redisBlacklistTokenRepository;
	private final RedisRefreshTokenRepository redisRefreshTokenRepository;

	@Override
	public Optional<BlacklistToken> findByAccessToken(String accessToken) {
		return redisBlacklistTokenRepository.findById(accessToken);
	}

	@Override
	public BlacklistToken save(BlacklistToken blacklistToken) {
		return redisBlacklistTokenRepository.save(blacklistToken);
	}

	@Override
	public Optional<RefreshToken> findByRefreshToken(String refreshToken) {
		return redisRefreshTokenRepository.findByRefreshToken(refreshToken);
	}

	@Override
	public RefreshToken save(RefreshToken refreshToken) {
		return redisRefreshTokenRepository.save(refreshToken);
	}

	@Override
	public void deleteByAccessToken(String accessToken) {
		redisRefreshTokenRepository.deleteById(accessToken);
	}
}
