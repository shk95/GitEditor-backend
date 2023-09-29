package com.shk95.giteditor.core.user.domain.token;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
	Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
