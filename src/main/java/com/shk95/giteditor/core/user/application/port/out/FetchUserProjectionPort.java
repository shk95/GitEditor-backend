package com.shk95.giteditor.core.user.application.port.out;

import com.shk95.giteditor.core.user.application.port.out.projection.DiscordIdProjection;
import com.shk95.giteditor.core.user.application.port.out.projection.OpenAIAccessTokenProjection;
import com.shk95.giteditor.core.user.application.port.out.projection.SimpleUserProjection;
import com.shk95.giteditor.core.user.application.port.out.projection.UserIdProjection;
import com.shk95.giteditor.core.user.domain.user.UserId;

import java.util.List;
import java.util.Optional;

public interface FetchUserProjectionPort {

	Optional<OpenAIAccessTokenProjection> fetchOpenAIAccessToken(UserId userId);

	List<SimpleUserProjection> fetchUserListLikeUsername(String username);

	Optional<UserIdProjection> fetchUserIdByDiscordId(String discordId);

	Optional<DiscordIdProjection> fetchDiscordIdByUserID(UserId userId);

}
