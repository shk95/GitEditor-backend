package com.shk95.giteditor.core.auth.application.port.out;

import com.shk95.giteditor.core.auth.domain.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepositoryPort {

	RefreshToken save(RefreshToken refreshToken);

	Optional<RefreshToken> findByRefreshToken(String refreshToken);

	void deleteByAccessToken(String accessToken);
}
