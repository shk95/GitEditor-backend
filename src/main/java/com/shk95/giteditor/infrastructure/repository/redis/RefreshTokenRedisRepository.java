package com.shk95.giteditor.infrastructure.repository.redis;

import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, Long> {
	RefreshToken findByRefreshToken(String refreshToken);
}
