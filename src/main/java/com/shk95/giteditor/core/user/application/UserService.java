package com.shk95.giteditor.core.user.application;

import com.shk95.giteditor.core.user.application.command.*;
import com.shk95.giteditor.common.security.jwt.GeneratedJwtToken;
import com.shk95.giteditor.core.user.domain.provider.Provider;
import com.shk95.giteditor.core.user.domain.user.SignupResult;

public interface UserService {
	SignupResult signupDefault(SignupCommand.Default command);

	Provider saveOAuthUser(SignupOAuthCommand command);

	GeneratedJwtToken loginDefault(LoginCommand login);

	GeneratedJwtToken reissue(ReissueCommand command);

	void logout(LogoutCommand command);
}
