package com.shk95.giteditor.core.user.application.port.out;

import com.shk95.giteditor.core.user.application.port.out.projection.OpenAIAccessToken;
import com.shk95.giteditor.core.user.domain.user.UserId;

import java.util.Optional;

public interface FetchUserProjectionPort {

	Optional<OpenAIAccessToken> fetchOpenAIAccessToken(UserId userId);
}
