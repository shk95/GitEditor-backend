package com.shk95.giteditor.domain.application;

import com.shk95.giteditor.domain.application.commands.LoginCommand;
import com.shk95.giteditor.domain.common.security.jwt.GeneratedJwtToken;
import com.shk95.giteditor.domain.application.commands.SignupOAuthCommand;
import com.shk95.giteditor.domain.model.provider.Provider;
import com.shk95.giteditor.web.apis.request.AuthRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService extends UserDetailsService {
	ResponseEntity<?> signupDefault(AuthRequest.Signup.Default signUp);

	Provider saveOAuthUser(SignupOAuthCommand command);

	GeneratedJwtToken defaultLogin(LoginCommand login, String ip);

	ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response);

	ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response);

	@Deprecated
	ResponseEntity<?> getAuthorities();
}
