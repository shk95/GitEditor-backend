package com.shk95.giteditor.core.auth.infrastructure;

import com.shk95.giteditor.core.auth.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RedisRefreshTokenRepository extends CrudRepository<RefreshToken, String> {

	Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
