package com.shk95.giteditor.web.apis;

import com.shk95.giteditor.domain.application.UserService;
import com.shk95.giteditor.domain.common.security.jwt.JwtTokenProvider;
import com.shk95.giteditor.utils.Resolver;
import com.shk95.giteditor.utils.Response;
import com.shk95.giteditor.web.payload.request.UserRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@CrossOrigin(origins = "http://localhost:4000", maxAge = 5000)
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	private final UserService usersService;
	private final JwtTokenProvider jwtTokenProvider;

	@PostMapping("/login")
	public ResponseEntity<?> login(@Validated @RequestBody UserRequestDto.Login loginDto, Errors errors
		, HttpServletRequest request, HttpServletResponse response) {
		// validation check
		if (errors.hasErrors()) {
			return Response.invalidFields(Resolver.error.inputFields(errors));
		}
		return usersService.defaultLogin(loginDto, request, response);
	}

	@PostMapping("/sign-up")
	public ResponseEntity<?> signUp(@Validated @RequestBody UserRequestDto.SignUp signUpDto, Errors errors) {
		// validation check
		if (errors.hasErrors()) {
			return Response.invalidFields(Resolver.error.inputFields(errors));
		}
		return usersService.defaultSignUp(signUpDto);
	}

	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
		String accessToken = jwtTokenProvider.resolveAccessToken(request);
		//TODO accessToken 검증

		//refreshToken 헤더에서 가져옴
		String refreshToken = jwtTokenProvider.resolveRefreshToken(request);


		return usersService.reissue(request, response);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
		return usersService.logout(request, response);
	}
}
