package com.shk95.giteditor.domain.application;

import com.shk95.giteditor.web.payload.request.UserRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService extends UserDetailsService {
	ResponseEntity<?> defaultSignUp(UserRequestDto.SignUp signUp);

	ResponseEntity<?> defaultLogin(UserRequestDto.Login login, HttpServletRequest request, HttpServletResponse response);

	ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response);

	ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response);

	@Deprecated
	ResponseEntity<?> getAuthorities();
}
