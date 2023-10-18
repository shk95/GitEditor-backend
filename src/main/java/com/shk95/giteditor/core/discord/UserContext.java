package com.shk95.giteditor.core.discord;

import com.shk95.giteditor.core.user.application.port.out.FetchUserProjectionPort;
import com.shk95.giteditor.core.user.infrastructure.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserContext {

	private final FetchUserProjectionPort fetchUserProjectionPort;
	private final JpaUserRepository jpaUserRepository;

	@Transactional(readOnly = true)
	public boolean isUserExists(String discordId) {
		return fetchUserProjectionPort.fetchUserIdByDiscordId(discordId).isPresent();
	}
}
