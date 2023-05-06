package com.shk95.giteditor.domain.application;

import com.shk95.giteditor.web.payload.request.UserRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
	ResponseEntity<?> signUp(UserRequestDto.SignUp signUp);

	ResponseEntity<?> login(UserRequestDto.Login login);

	ResponseEntity<?> reissue(UserRequestDto.Reissue reissue);

	ResponseEntity<?> logout(UserRequestDto.Logout logout);

	@Deprecated
	ResponseEntity<?> authority();
}
