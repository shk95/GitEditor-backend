package com.shk95.giteditor.domain.application;

import com.shk95.giteditor.domain.application.commands.*;
import com.shk95.giteditor.domain.common.security.jwt.GeneratedJwtToken;
import com.shk95.giteditor.domain.model.provider.Provider;
import com.shk95.giteditor.domain.model.user.SignupResult;

public interface UserService {
	SignupResult signupDefault(SignupCommand.Default command);

	Provider saveOAuthUser(SignupOAuthCommand command);

	GeneratedJwtToken loginDefault(LoginCommand login);

	GeneratedJwtToken reissue(ReissueCommand command);

	void logout(LogoutCommand command);
}
