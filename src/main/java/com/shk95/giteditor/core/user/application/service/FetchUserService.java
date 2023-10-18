package com.shk95.giteditor.core.user.application.service;

import com.shk95.giteditor.core.user.application.port.in.FetchUserInfoUseCase;
import com.shk95.giteditor.core.user.application.port.out.FetchUserProjectionPort;
import com.shk95.giteditor.core.user.application.port.out.UserCrudRepositoryPort;
import com.shk95.giteditor.core.user.application.port.out.projection.DiscordIdProjection;
import com.shk95.giteditor.core.user.application.port.out.projection.OpenAIAccessTokenProjection;
import com.shk95.giteditor.core.user.domain.user.User;
import com.shk95.giteditor.core.user.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class FetchUserService implements FetchUserInfoUseCase {

	private final UserCrudRepositoryPort userCrudRepositoryPort;
	private final FetchUserProjectionPort fetchUserProjectionPort;

	@Transactional(readOnly = true)
	@Override
	public String fetchDiscordIdByUserId(UserId userId) {
		return fetchUserProjectionPort.fetchDiscordIdByUserID(userId)
			.map(DiscordIdProjection::getDiscordId)
			.orElse("");
	}

	@Transactional(readOnly = true)
	@Override
	public Optional<User> fetchUser(UserId userId) {
		return userCrudRepositoryPort.findUserWithProviderById(userId);
	}

	@Transactional(readOnly = true)
	@Override
	public String fetchOpenAIAccessToken(UserId userId) {
		return fetchUserProjectionPort.fetchOpenAIAccessToken(userId)
			.map(OpenAIAccessTokenProjection::getOpenAIToken)
			.orElse("");
	}
}
