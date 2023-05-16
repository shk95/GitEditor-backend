package com.shk95.giteditor.web.apis;

import com.shk95.giteditor.domain.application.UserService;
import com.shk95.giteditor.utils.Resolver;
import com.shk95.giteditor.utils.Response;
import com.shk95.giteditor.web.apis.request.UserRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {

	private final UserService usersService;

	@PostMapping("/login")
	public ResponseEntity<?> login(@Validated @RequestBody UserRequestDto.Login loginDto, Errors errors
		, HttpServletRequest request, HttpServletResponse response) {
		// validation check
		if (errors.hasErrors()) {
			return Response.invalidFields(Resolver.error.inputFields(errors));
		}
		return usersService.defaultLogin(request, response, loginDto);
	}

	@PostMapping("/signup")
	public ResponseEntity<?> signUp(@Validated @RequestBody UserRequestDto.SignUp signUpDto, Errors errors) {
		log.debug("##### {} invoked", this.getClass().getName());
		// validation check
		if (errors.hasErrors()) {
			return Response.invalidFields(Resolver.error.inputFields(errors));
		}
		return usersService.defaultSignUp(signUpDto);
	}

	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
		return usersService.reissue(request, response);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
		return usersService.logout(request, response);
	}
}
