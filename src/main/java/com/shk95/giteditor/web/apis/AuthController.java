package com.shk95.giteditor.web.apis;

import com.shk95.giteditor.domain.application.UserService;
import com.shk95.giteditor.utils.Resolver;
import com.shk95.giteditor.utils.Response;
import com.shk95.giteditor.web.payload.request.UserRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:4000", maxAge = 5000)
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	private final UserService usersService;


	@PostMapping("/login")
	public ResponseEntity<?> login(@Validated @RequestBody UserRequestDto.Login login, Errors errors
		, HttpServletRequest request) {
		// validation check
		if (errors.hasErrors()) {
			return Response.invalidFields(Resolver.error.inputFields(errors));
		}
		return usersService.login(login, request);
	}

	@PostMapping("/sign-up")
	public ResponseEntity<?> signUp(@Validated @RequestBody UserRequestDto.SignUp signUp, Errors errors) {
		// validation check
		if (errors.hasErrors()) {
			return Response.invalidFields(Resolver.error.inputFields(errors));
		}
		return usersService.signUp(signUp);
	}

	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(@Validated @RequestBody UserRequestDto.Reissue reissue, Errors errors
		, HttpServletRequest request) {
		// validation check
		if (errors.hasErrors()) {
			return Response.invalidFields(Resolver.error.inputFields(errors));
		}
		return usersService.reissue(reissue, request);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@Validated @RequestBody UserRequestDto.Logout logout, Errors errors) {
		// validation check
		if (errors.hasErrors()) {
			return Response.invalidFields(Resolver.error.inputFields(errors));
		}
		return usersService.logout(logout);
	}
}
