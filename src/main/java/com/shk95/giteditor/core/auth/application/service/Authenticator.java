package com.shk95.giteditor.core.auth.application.service;

import com.shk95.giteditor.common.exception.TokenValidFailedException;
import com.shk95.giteditor.common.security.jwt.GeneratedJwtToken;
import com.shk95.giteditor.common.security.jwt.JwtTokenProvider;
import com.shk95.giteditor.core.auth.application.port.in.AuthenticateUseCase;
import com.shk95.giteditor.core.auth.application.port.out.BlacklistTokenRepositoryPort;
import com.shk95.giteditor.core.auth.application.port.out.LoadUserPort;
import com.shk95.giteditor.core.auth.application.port.out.RefreshTokenRepositoryPort;
import com.shk95.giteditor.core.auth.application.service.command.LoginCommand;
import com.shk95.giteditor.core.auth.application.service.command.LogoutCommand;
import com.shk95.giteditor.core.auth.application.service.command.ReissueCommand;
import com.shk95.giteditor.core.auth.domain.BlacklistToken;
import com.shk95.giteditor.core.auth.domain.CustomUserDetails;
import com.shk95.giteditor.core.auth.domain.RefreshToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class Authenticator implements AuthenticateUseCase {

	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final JwtTokenProvider jwtTokenProvider;

	private final BlacklistTokenRepositoryPort blacklistTokenRepositoryPort;
	private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
	private final LoadUserPort loadUserPort;

	@Override
	public GeneratedJwtToken login(LoginCommand login) {// 브라우저에서 진행되는 기본 사용자 id password 인증방식
		// 로그인 인증정보 가져옴.
		CustomUserDetails userDetails = loadUserPort.loadUser(login.getUserId());
		// 인증용 토큰 생성
		UsernamePasswordAuthenticationToken authenticationToken
			= new UsernamePasswordAuthenticationToken(
			userDetails
			, login.getPassword()
			, userDetails.getAuthorities());
		// 인증
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		log.debug("### {} authentication.getName(must be user's id) : [{}]", this.getClass().getName(), authentication.getName());

		// 인증 정보를 기반으로 JWT 토큰 생성
		GeneratedJwtToken generatedJwtToken = jwtTokenProvider.generateToken(authentication);

		// Redis RefreshToken 저장
		refreshTokenRepositoryPort.save(
			RefreshToken.builder()// TODO : redis transaction 관리
				.accessToken(generatedJwtToken.getAccessToken())
				.refreshToken(generatedJwtToken.getRefreshToken())
				.build());
		return generatedJwtToken;
	}

	@Override
	public GeneratedJwtToken reissue(ReissueCommand command) {
		GeneratedJwtToken renewedToken;
		try {
			renewedToken = jwtTokenProvider.generateToken(jwtTokenProvider.getAuthentication(command.getAccessToken()));
		} catch (Exception e) {
			throw new TokenValidFailedException();
		}
		refreshTokenRepositoryPort.deleteByAccessToken(command.getAccessToken());
		// Redis RefreshToken update
		refreshTokenRepositoryPort.save(
			RefreshToken.builder()
				.accessToken(renewedToken.getAccessToken())
				.refreshToken(renewedToken.getRefreshToken())
				.build());
		return renewedToken;
	}

	@Override
	public void logout(LogoutCommand command) {
		refreshTokenRepositoryPort.deleteByAccessToken(command.getAccessToken());
		// 해당 Access Token 유효시간 가지고 와서 BlackList 로 저장하기
		Long expiration = jwtTokenProvider.getExpiration(command.getAccessToken());
		if (expiration > 0) {
			blacklistTokenRepositoryPort.save(BlacklistToken.builder()
				.accessToken(command.getAccessToken())
				.expiration(expiration).build());
		}
	}
}
