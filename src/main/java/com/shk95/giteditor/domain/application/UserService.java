package com.shk95.giteditor.domain.application;

import com.shk95.giteditor.web.payload.request.UserRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.http.HttpServletRequest;

public interface UserService extends UserDetailsService {
	ResponseEntity<?> signUp(UserRequestDto.SignUp signUp);

	ResponseEntity<?> login(UserRequestDto.Login login, HttpServletRequest request);

	ResponseEntity<?> reissue(UserRequestDto.Reissue reissue, HttpServletRequest request);

	ResponseEntity<?> logout(UserRequestDto.Logout logout);

	@Deprecated
	ResponseEntity<?> authority();
}
