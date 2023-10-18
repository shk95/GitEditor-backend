package com.shk95.giteditor.core.user.application.port.in;

import com.shk95.giteditor.core.user.domain.user.User;
import com.shk95.giteditor.core.user.domain.user.UserId;

import java.util.Optional;

public interface FetchUserInfoUseCase {

	String fetchDiscordIdByUserId(UserId userId);

	Optional<User> fetchUser(UserId userId);

	String fetchOpenAIAccessToken(UserId userId);
}
