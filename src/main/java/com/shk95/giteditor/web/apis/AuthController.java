package com.shk95.giteditor.web.apis;

import com.shk95.giteditor.domain.application.UserService;
import com.shk95.giteditor.domain.application.commands.LoginCommand;
import com.shk95.giteditor.domain.application.commands.SignupOAuthCommand;
import com.shk95.giteditor.domain.common.constant.ProviderType;
import com.shk95.giteditor.domain.common.security.jwt.GeneratedJwtToken;
import com.shk95.giteditor.domain.model.provider.Provider;
import com.shk95.giteditor.domain.model.provider.ProviderLoginInfo;
import com.shk95.giteditor.domain.model.provider.ProviderLoginInfoRepository;
import com.shk95.giteditor.utils.CookieUtil;
import com.shk95.giteditor.utils.Helper;
import com.shk95.giteditor.utils.Resolver;
import com.shk95.giteditor.utils.Response;
import com.shk95.giteditor.web.apis.request.AuthRequest;
import com.shk95.giteditor.web.validator.EmailValidator;
import com.shk95.giteditor.web.validator.UserIdValidator;
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Optional;

import static com.shk95.giteditor.config.ConstantFields.Jwt.ExpireTime.REFRESH_TOKEN_EXPIRE_TIME;
import static com.shk95.giteditor.config.ConstantFields.Jwt.TYPE_REFRESH;
import static com.shk95.giteditor.config.ConstantFields.REDIRECT_SIGNUP_OAUTH_ID;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {

	private final UserService userService;
	private final ProviderLoginInfoRepository providerLoginInfoRepository;
	private final EmailValidator emailValidator;
	private final UserIdValidator userIdValidator;

	@PostMapping("/login")
	public ResponseEntity<?> login(@Validated @RequestBody AuthRequest.Login login, Errors errors
		, HttpServletRequest request, HttpServletResponse response) {
		// validation check
		if (errors.hasErrors()) {
			return Response.invalidFields(Resolver.error.inputFields(errors));
		}
		String clientIp = Helper.getClientIp(request);

		GeneratedJwtToken tokenInfo = userService.loginDefault(LoginCommand.of(login), clientIp);
		CookieUtil.addCookie(response, TYPE_REFRESH, tokenInfo.getRefreshToken(), (int) (REFRESH_TOKEN_EXPIRE_TIME / 1000));

		return Response.success(tokenInfo, "로그인에 성공했습니다.", HttpStatus.OK);
	}


	@PostMapping("/signup")
	public ResponseEntity<?> signup(@Validated @RequestBody AuthRequest.Signup.Default signup, Errors errors) {
		emailValidator.validate(signup, errors);
		userIdValidator.setProviderType(ProviderType.LOCAL);
		userIdValidator.validate(signup, errors);
		// validation check
		if (errors.hasErrors()) {
			return Response.invalidFields(Resolver.error.inputFields(errors));
		}
		return userService.signupDefault(signup);
	}

	@PostMapping("/signup/oauth")
	public ResponseEntity<?> signup(@Validated @RequestBody AuthRequest.Signup.OAuth oAuthSignup, Errors errors,
									HttpServletRequest request, HttpServletResponse response) {
		Cookie cookie = CookieUtil.getCookie(request, REDIRECT_SIGNUP_OAUTH_ID).orElse(null);
		if (cookie == null) {
			return Response.fail("회원가입 시간 초과", HttpStatus.BAD_REQUEST);
		}
		String oAuthId = CookieUtil.deserialize(cookie, String.class);
		Optional<ProviderLoginInfo> oAuthLoginInfo = providerLoginInfoRepository.findById(oAuthId);
		if (!oAuthLoginInfo.isPresent()) {
			return Response.fail("회원가입 시간 초과", HttpStatus.BAD_REQUEST);
		}
		userIdValidator.setProviderType(ProviderType.valueOf(oAuthLoginInfo.get().getProviderType()));
		userIdValidator.validate(oAuthId, errors);
		if (errors.hasErrors()) {
			return Response.invalidFields(Resolver.error.inputFields(errors));
		}

		SignupOAuthCommand command = new SignupOAuthCommand().set(oAuthSignup).set(oAuthLoginInfo.get());
		Provider savedUser = userService.saveOAuthUser(command);

		CookieUtil.deleteCookie(request, response, REDIRECT_SIGNUP_OAUTH_ID);
		providerLoginInfoRepository.deleteById(oAuthId);
		return Response.success("회원가입이 완료되었습니다.");
	}

	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
		return userService.reissue(request, response);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
		return userService.logout(request, response);
	}
}
