package com.shk95.giteditor.infrastructure.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, Long> {
	RefreshToken findByRefreshToken(String refreshToken);

	void deleteRefreshTokenById(String userId);
}
