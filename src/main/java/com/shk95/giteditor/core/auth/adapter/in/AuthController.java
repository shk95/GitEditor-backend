package com.shk95.giteditor.core.auth.adapter.in;

import com.shk95.giteditor.common.security.CurrentUser;
import com.shk95.giteditor.common.security.jwt.GeneratedJwtToken;
import com.shk95.giteditor.common.security.jwt.JwtTokenProvider;
import com.shk95.giteditor.common.utils.Resolver;
import com.shk95.giteditor.common.utils.Response;
import com.shk95.giteditor.common.utils.web.CookieUtil;
import com.shk95.giteditor.common.utils.web.Helper;
import com.shk95.giteditor.core.auth.application.port.in.AuthenticateUseCase;
import com.shk95.giteditor.core.auth.application.port.out.RefreshTokenRepositoryPort;
import com.shk95.giteditor.core.auth.application.service.command.LoginCommand;
import com.shk95.giteditor.core.auth.application.service.command.LogoutCommand;
import com.shk95.giteditor.core.auth.application.service.command.ReissueCommand;
import com.shk95.giteditor.core.auth.domain.CustomUserDetails;
import com.shk95.giteditor.core.auth.domain.RefreshToken;
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

import static com.shk95.giteditor.config.Constants.Jwt.ExpireTime.REFRESH_TOKEN_EXPIRE_TIME;
import static com.shk95.giteditor.config.Constants.Jwt.JWT_TYPE_REFRESH;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {

	private final JwtTokenProvider jwtTokenProvider;

	private final AuthenticateUseCase authenticateUseCase;
	private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;

	@PostMapping("/login")
	public ResponseEntity<?> login(@Validated @RequestBody AuthRequest.Login login, Errors errors, HttpServletResponse response) {
		// validation check
		if (errors.hasErrors()) {
			return Response.invalidFields(Resolver.error.inputFields(errors));
		}

		GeneratedJwtToken tokenInfo = authenticateUseCase.login(LoginCommand.of(login));
		CookieUtil.addCookie(response, JWT_TYPE_REFRESH, tokenInfo.getRefreshToken(), (int) (REFRESH_TOKEN_EXPIRE_TIME / 1000));

		return Response.success(tokenInfo, "로그인에 성공했습니다.", HttpStatus.OK);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@CurrentUser CustomUserDetails userDetails,
	                                HttpServletRequest request, HttpServletResponse response) {
		String accessToken = jwtTokenProvider.resolveAccessToken(request);
		String refreshToken = jwtTokenProvider.resolveRefreshToken(request);
		String currentIpAddress = Helper.getClientIp(request);

		authenticateUseCase.logout(LogoutCommand.builder()
			.accessToken(accessToken).refreshToken(refreshToken).ip(currentIpAddress).build());
		CookieUtil.deleteCookie(request, response, JWT_TYPE_REFRESH);
		log.info("User Logout. name: [{}]", userDetails.getUsername());
		return Response.success("로그아웃되었습니다.");
	}

	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
		String accessToken = jwtTokenProvider.resolveAccessToken(request);
		String refreshToken = jwtTokenProvider.resolveRefreshToken(request);

		//FIXME: 리펙토링
		/*
		 * 검증목록
		 * 1. access token claim key, type, subject, expiration
		 * 2. refresh token expiration, type
		 *
		 * refresh token 검증
		 * access token 에서 subject(user id) 로 refresh token repository 에서 검색
		 * 현재 ip 와 refresh token repository 의 ip 일치 확인 -> 확인 필요
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
		Optional<RefreshToken> savedRefreshToken = refreshTokenRepositoryPort
			.findByRefreshToken(refreshToken);
		if (savedRefreshToken.isEmpty()) {
			return Response.fail("Refresh Token 정보와 Access Token 정보가 일치하지 않습니다.", HttpStatus.NOT_ACCEPTABLE);
		}
		if (!accessToken.equals(savedRefreshToken.get().getAccessToken())) {
			return Response.fail("Refresh Token 정보와 Access Token 정보가 일치하지 않습니다.", HttpStatus.NOT_ACCEPTABLE);
		}

		GeneratedJwtToken tokenInfo = authenticateUseCase.reissue(new ReissueCommand(accessToken));

		CookieUtil.deleteCookie(request, response, JWT_TYPE_REFRESH);
		CookieUtil.addCookie(response, JWT_TYPE_REFRESH, tokenInfo.getRefreshToken(), (int) (REFRESH_TOKEN_EXPIRE_TIME / 1000));
		return Response.success(tokenInfo, "토큰이 갱신되었습니다.", HttpStatus.OK);
	}
}
