package com.shk95.giteditor.core.user.application.port.in;

import com.shk95.giteditor.core.user.adapter.in.SignupResult;
import com.shk95.giteditor.core.user.application.service.command.SignupCommand;
import com.shk95.giteditor.core.user.application.service.command.SignupOAuthCommand;
import com.shk95.giteditor.core.user.domain.provider.Provider;

public interface SignupUseCase {
	SignupResult signupDefault(SignupCommand.Default command);

	Provider signupOAuthUser(SignupOAuthCommand command);

}
