package com.shk95.giteditor.core.auth.application.port.in;

import com.shk95.giteditor.common.security.jwt.GeneratedJwtToken;
import com.shk95.giteditor.core.auth.application.service.command.LoginCommand;
import com.shk95.giteditor.core.auth.application.service.command.LogoutCommand;
import com.shk95.giteditor.core.auth.application.service.command.ReissueCommand;

public interface AuthenticateUseCase {

	GeneratedJwtToken login(LoginCommand login);// Default Login

	void logout(LogoutCommand command);

	GeneratedJwtToken reissue(ReissueCommand command);
}
