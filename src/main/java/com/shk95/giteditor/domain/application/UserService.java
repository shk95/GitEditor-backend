package com.shk95.giteditor.domain.application;

import com.shk95.giteditor.domain.application.commands.LoginCommand;
import com.shk95.giteditor.domain.application.commands.LogoutCommand;
import com.shk95.giteditor.domain.application.commands.ReissueCommand;
import com.shk95.giteditor.domain.application.commands.SignupOAuthCommand;
import com.shk95.giteditor.domain.common.security.jwt.GeneratedJwtToken;
import com.shk95.giteditor.domain.model.provider.Provider;
import com.shk95.giteditor.web.apis.request.AuthRequest;
import org.springframework.http.ResponseEntity;

public interface UserService {
	ResponseEntity<?> signupDefault(AuthRequest.Signup.Default signUp);

	Provider saveOAuthUser(SignupOAuthCommand command);

	GeneratedJwtToken loginDefault(LoginCommand login);

	GeneratedJwtToken reissue(ReissueCommand command);

	void logout(LogoutCommand command);
}
