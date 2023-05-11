package com.shk95.giteditor.domain.model.token;

import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
	RefreshToken findByRefreshToken(String refreshToken);

	void deleteRefreshTokenByRefreshToken(String refreshToken);
}
