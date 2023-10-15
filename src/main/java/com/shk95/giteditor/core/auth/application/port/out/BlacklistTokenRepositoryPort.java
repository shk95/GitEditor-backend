package com.shk95.giteditor.core.auth.application.port.out;

import com.shk95.giteditor.core.auth.domain.BlacklistToken;

import java.util.Optional;

public interface BlacklistTokenRepositoryPort {

	Optional<BlacklistToken> findByAccessToken(String accessToken);

	BlacklistToken save(BlacklistToken blacklistToken);
}
