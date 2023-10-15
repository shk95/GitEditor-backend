package com.shk95.giteditor.core.user.adapter.in;

import com.shk95.giteditor.common.constant.ProviderType;
import com.shk95.giteditor.common.utils.Resolver;
import com.shk95.giteditor.common.utils.Response;
import com.shk95.giteditor.common.utils.web.CookieUtil;
import com.shk95.giteditor.core.auth.adapter.in.AuthRequest;
import com.shk95.giteditor.core.user.adapter.in.validation.EmailValidator;
import com.shk95.giteditor.core.user.adapter.in.validation.UserIdValidator;
import com.shk95.giteditor.core.user.application.port.in.SignupUseCase;
import com.shk95.giteditor.core.user.application.port.out.OAuthTokenHolderPort;
import com.shk95.giteditor.core.user.application.service.command.SignupCommand;
import com.shk95.giteditor.core.user.application.service.command.SignupOAuthCommand;
import com.shk95.giteditor.core.user.domain.provider.Provider;
import com.shk95.giteditor.core.user.domain.provider.ProviderLoginInfo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.shk95.giteditor.config.Constants.REDIRECT_SIGNUP_OAUTH_ID;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class SignupController {

	private final SignupUseCase signupUseCase;
	private final OAuthTokenHolderPort oAuthTokenHolderPort;

	private final EmailValidator emailValidator;
	private final UserIdValidator userIdValidator;

	@PostMapping("/signup")
	public ResponseEntity<?> signup(@Validated @RequestBody AuthRequest.Signup.Default signup, Errors errors) {
		SignupCommand.Default command = SignupCommand.Default.builder()
			.userId(signup.getUserId())
			.password(signup.getPassword())
			.defaultEmail(signup.getDefaultEmail())
			.username(signup.getUsername())
			.build();
		// validate
		userIdValidator.setProviderType(ProviderType.LOCAL);
		userIdValidator.validate(command, errors);
		emailValidator.validate(command, errors);
		if (errors.hasErrors()) {
			return Response.invalidFields(Resolver.error.inputFields(errors));
		}
		SignupResult result = signupUseCase.signupDefault(command);
		return result.isStatus()
			? Response.success(result.getMessage())
			: Response.fail(result.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@PostMapping("/signup/oauth")
	public ResponseEntity<?> signup(@Validated @RequestBody AuthRequest.Signup.OAuth oAuthSignup, Errors errors,
	                                HttpServletRequest request, HttpServletResponse response) {
		Cookie cookie = CookieUtil.getCookie(request, REDIRECT_SIGNUP_OAUTH_ID).orElse(null);
		if (cookie == null) {
			return Response.fail("회원가입 시간 초과", HttpStatus.BAD_REQUEST);
		}
		String oAuthId = CookieUtil.deserialize(cookie, String.class);
		Optional<ProviderLoginInfo> oAuthLoginInfo = oAuthTokenHolderPort.findById(oAuthId);
		if (oAuthLoginInfo.isEmpty()) {
			return Response.fail("회원가입 시간 초과", HttpStatus.BAD_REQUEST);
		}
		userIdValidator.setProviderType(ProviderType.valueOf(oAuthLoginInfo.get().getProviderType()));
		userIdValidator.validate(oAuthId, errors);
		if (errors.hasErrors()) {
			return Response.invalidFields(Resolver.error.inputFields(errors));
		}

		SignupOAuthCommand command = new SignupOAuthCommand().setSignupInfo(oAuthSignup).setProviderLoginInfo(oAuthLoginInfo.get());
		Provider savedUser = signupUseCase.signupOAuthUser(command);

		CookieUtil.deleteCookie(request, response, REDIRECT_SIGNUP_OAUTH_ID);
		oAuthTokenHolderPort.deleteById(oAuthId);
		return Response.success("회원가입이 완료되었습니다.");
	}
}
