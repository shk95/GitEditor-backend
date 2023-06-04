package com.shk95.giteditor.web.apis;

import com.shk95.giteditor.domain.application.UserService;
import com.shk95.giteditor.domain.application.commands.*;
import com.shk95.giteditor.domain.common.constant.ProviderType;
import com.shk95.giteditor.domain.common.security.CurrentUser;
import com.shk95.giteditor.domain.common.security.jwt.GeneratedJwtToken;
import com.shk95.giteditor.domain.common.security.jwt.JwtTokenProvider;
import com.shk95.giteditor.domain.model.provider.Provider;
import com.shk95.giteditor.domain.model.provider.ProviderLoginInfo;
import com.shk95.giteditor.domain.model.provider.ProviderLoginInfoRepository;
import com.shk95.giteditor.domain.model.token.RefreshToken;
import com.shk95.giteditor.domain.model.token.RefreshTokenRepository;
import com.shk95.giteditor.domain.model.user.CustomUserDetails;
import com.shk95.giteditor.domain.model.user.SignupResult;
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
import static com.shk95.giteditor.config.ConstantFields.Jwt.JWT_TYPE_REFRESH;
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
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;

	@PostMapping("/login")
	public ResponseEntity<?> login(@Validated @RequestBody AuthRequest.Login login, Errors errors
		, HttpServletRequest request, HttpServletResponse response) {
		// validation check
		if (errors.hasErrors()) {
			return Response.invalidFields(Resolver.error.inputFields(errors));
		}
		String clientIp = Helper.getClientIp(request);

		GeneratedJwtToken tokenInfo = userService.loginDefault(LoginCommand.of(login, clientIp));
		CookieUtil.addCookie(response, JWT_TYPE_REFRESH, tokenInfo.getRefreshToken(), (int) (REFRESH_TOKEN_EXPIRE_TIME / 1000));

		return Response.success(tokenInfo, "로그인에 성공했습니다.", HttpStatus.OK);
	}

	@PostMapping("/signup")
	public ResponseEntity<?> signup(@Validated @RequestBody AuthRequest.Signup.Default signup, Errors errors) {
		SignupCommand.Default command = SignupCommand.Default.builder()
			.userId(signup.getUserId())
			.password(signup.getPassword())
			.defaultEmail(signup.getDefaultEmail())
			.username(signup.getUsername())
			.build();
		// validation check
		userIdValidator.setProviderType(ProviderType.LOCAL);
		userIdValidator.validate(command, errors);
		emailValidator.validate(command, errors);
		if (errors.hasErrors()) {
			return Response.invalidFields(Resolver.error.inputFields(errors));
		}
		SignupResult result = userService.signupDefault(command);
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
		String accessToken = jwtTokenProvider.resolveAccessToken(request);
		String refreshToken = jwtTokenProvider.resolveRefreshToken(request);
		String currentIpAddress = Helper.getClientIp(request);

		//FIXME: 리펙토링
		/*
		 * 검증목록
		 * 1. access token claim key, type, subject, expiration
		 * 2. refresh token expiration, type
		 *
		 * refresh token 검증
		 * access token 에서 subject(user id) 로 refresh token repository 에서 검색
		 * 현재 ip 와 refresh token repository 의 ip 일치 확인
		 *
		 * access token 에 유지되야할 목록 : subject(user id), claim(authorities)
		 */

		// Refresh Token 검증. 실패시 로그아웃 상태이다.
		if (!jwtTokenProvider.isVerified(refreshToken)) {
			return Response.fail("Refresh Token 정보가 유효하지 않습니다.", HttpStatus.NOT_ACCEPTABLE);
		}
		if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
			return Response.fail("Refresh Token 이 아닙니다.", HttpStatus.NOT_ACCEPTABLE);
		}
		// Refresh token 정보 가져오기.
		Optional<RefreshToken> savedRefreshToken = refreshTokenRepository
			.findById(jwtTokenProvider.getClaims(accessToken).getSubject());
		if (!savedRefreshToken.isPresent()) {
			return Response.fail("Refresh Token 정보와 Access Token 정보가 일치하지 않습니다.", HttpStatus.NOT_ACCEPTABLE);
		}
		// 최초 로그인한 ip 와 같은지 확인. (처리 방식에 따라 재발급을 하지 않거나 메일 등의 알림을 주는 방법이 있음)
		if (!savedRefreshToken.get().getIp().equals(currentIpAddress)) {
			refreshTokenRepository.delete(savedRefreshToken.get());
			return Response.fail("IP 주소가 다릅니다.", HttpStatus.NOT_ACCEPTABLE);
		}

		GeneratedJwtToken tokenInfo = userService.reissue(
			new ReissueCommand(accessToken, refreshToken, currentIpAddress));

		CookieUtil.deleteCookie(request, response, JWT_TYPE_REFRESH);
		CookieUtil.addCookie(response, JWT_TYPE_REFRESH, tokenInfo.getRefreshToken(), (int) (REFRESH_TOKEN_EXPIRE_TIME / 1000));
		return Response.success(tokenInfo, "토큰이 갱신되었습니다.", HttpStatus.OK);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@CurrentUser CustomUserDetails userDetails,
									HttpServletRequest request, HttpServletResponse response) {
		String accessToken = jwtTokenProvider.resolveAccessToken(request);
		String refreshToken = jwtTokenProvider.resolveRefreshToken(request);
		String currentIpAddress = Helper.getClientIp(request);

		userService.logout(LogoutCommand.builder()
			.accessToken(accessToken).refreshToken(refreshToken).ip(currentIpAddress).build());
		CookieUtil.deleteCookie(request, response, JWT_TYPE_REFRESH);
		log.info("User Logout. name: [{}]", userDetails.getUsername());
		return Response.success("로그아웃되었습니다.");
	}
}
