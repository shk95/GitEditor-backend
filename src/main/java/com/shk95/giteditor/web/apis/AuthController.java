package com.shk95.giteditor.web.apis;

import com.shk95.giteditor.domain.application.UserService;
import com.shk95.giteditor.utils.Resolver;
import com.shk95.giteditor.web.payload.request.UserRequestDto;
import com.shk95.giteditor.utils.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	private final UserService usersService;


	@PostMapping("/login")
	public ResponseEntity<?> login(@Validated @RequestBody UserRequestDto.Login login, Errors errors) {
		// validation check
		if (errors.hasErrors()) {
			return Response.invalidFields(Resolver.error.inputFields(errors));
		}
		return usersService.login(login);
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
	public ResponseEntity<?> reissue(@Validated @RequestBody UserRequestDto.Reissue reissue, Errors errors) {
		// validation check
		if (errors.hasErrors()) {
			return Response.invalidFields(Resolver.error.inputFields(errors));
		}
		return usersService.reissue(reissue);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@Validated @RequestBody UserRequestDto.Logout logout, Errors errors) {
		// validation check
		if (errors.hasErrors()) {
			return Response.invalidFields(Resolver.error.inputFields(errors));
		}
		return usersService.logout(logout);
	}

	//	private final JwtUtils jwtUtils;
	//	private final AuthenticationManager authenticationManager;
	/*
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequestDto.SignUp signUpRequest, Errors errors) {
		if (errors.hasErrors()) {
			return response.fail(errors);
		}

		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return Result.failure("Error: Username is already taken!");
		}

		if (userRepository.existsByEmailAddress(signUpRequest.getEmailAddress())) {
			return Result.failure("Error: Email is already in use!");
		}

		// Create new user's account
		User user = User.create(signUpRequest.getUsername(),
			signUpRequest.getEmailAddress(),
			encoder.encode(signUpRequest.getPassword()));
		user.setRole(Authority.ROLE_USER);

		TODO 임시
		Set<String> strRoles = new HashSet<>();
		strRoles.add("user");
		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(Role.ROLE_USER)
				.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
					case "admin":
						Role adminRole = roleRepository.findByName(Role.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(adminRole);

						break;
					default:
						Role userRole = roleRepository.findByName(Role.ROLE_USER)
							.orElse(Role.ROLE_USER);
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
						roles.add(userRole);
				}
			});
		}
		user.setRoles(roles);

		return userServiceImpl.signup(user);
	}
	*/
}
