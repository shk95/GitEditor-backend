package com.shk95.giteditor.domain.application.impl;

import com.shk95.giteditor.domain.application.UserService;
import com.shk95.giteditor.domain.application.commands.TokenResolverCommand;
import com.shk95.giteditor.domain.common.constants.Role;
import com.shk95.giteditor.domain.common.mail.MailManager;
import com.shk95.giteditor.domain.common.mail.MessageVariable;
import com.shk95.giteditor.domain.common.security.CustomUserDetails;
import com.shk95.giteditor.domain.common.security.jwt.JwtTokenProvider;
import com.shk95.giteditor.domain.model.token.BlacklistToken;
import com.shk95.giteditor.domain.model.token.BlacklistTokenRepository;
import com.shk95.giteditor.domain.model.token.RefreshToken;
import com.shk95.giteditor.domain.model.token.RefreshTokenRepository;
import com.shk95.giteditor.domain.model.user.User;
import com.shk95.giteditor.domain.model.user.UserFinder;
import com.shk95.giteditor.domain.model.user.UserRepository;
import com.shk95.giteditor.utils.Helper;
import com.shk95.giteditor.utils.Response;
import com.shk95.giteditor.utils.SecurityUtil;
import com.shk95.giteditor.web.apis.request.UserRequestDto;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	private final BlacklistTokenRepository blacklistTokenRepository;
	private final SecurityUtil securityUtil;
	private final MailManager mailManager;
	private final UserFinder userFinder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("### login user : [{}]", username);
		User user = userFinder.find(username).orElse(null);
		if (user == null) {
			throw new UsernameNotFoundException("Cannot find user. user : [" + username + "]");
		}
		return CustomUserDetails.createUserDetailsBuilder(user).build();
	}

	@Override
	@Transactional
	public ResponseEntity<?> defaultSignUp(UserRequestDto.SignUp signUp) {
		if (userRepository.existsByDefaultEmail(signUp.getDefaultEmail())) {
			return Response.fail("이미 회원가입된 이메일 입니다.", HttpStatus.BAD_REQUEST);
		}
		if (userRepository.existsByUserId(signUp.getUserId())) {
			return Response.fail("이미 회원가입된 아이디 입니다.", HttpStatus.BAD_REQUEST);
		}
		userRepository.save(User.builder()
			.userId(signUp.getUserId())
			.password(passwordEncoder.encode(signUp.getPassword()))
			.defaultEmail(signUp.getDefaultEmail())
			.role(Role.USER)
			.username(signUp.getUsername())
			.build());
//		sendWelcomeMessage(user);
		return Response.success("회원가입에 성공했습니다.");
	}

	@Override
	public ResponseEntity<?> defaultLogin(HttpServletRequest request, HttpServletResponse response
		, UserRequestDto.Login login) {
		// 로그인 인증정보 가져옴.
		CustomUserDetails userDetails = (CustomUserDetails) this.loadUserByUsername(login.getUserId());//TODO: 예외처리
		// 인증용 토큰 생성
		UsernamePasswordAuthenticationToken authenticationToken
			= new UsernamePasswordAuthenticationToken(
			userDetails.getUserId()
			, login.getPassword()
			, userDetails.getAuthorities());
		// 인증
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		log.debug("### {} authentication.getName(must be user's id) : [{}]", this.getClass().getName(), authentication.getName());
		// 인증 정보를 기반으로 JWT 토큰 생성
		TokenResolverCommand.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

		// Redis RefreshToken 저장
		refreshTokenRepository.save(RefreshToken.builder()// TODO : redis transaction 설정
			.userId(authentication.getName())
			.ip(Helper.getClientIp(request))
			.authorities(authentication.getAuthorities())
			.refreshToken(tokenInfo.getRefreshToken())
			.build());
		return Response.success(tokenInfo, "로그인에 성공했습니다.", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
		final String accessToken = jwtTokenProvider.resolveAccessToken(request); //TODO: reissue: accessToken 검증
		final String refreshToken = jwtTokenProvider.resolveRefreshToken(request); //TODO: reissue: refreshToken cookie 예외처리
		Assert.notNull(accessToken, "access token may not be null");
		Assert.notNull(refreshToken, "refresh token may not be null");

		final Claims accessTokenClaims = jwtTokenProvider.getClaims(accessToken);

		final String resolvedUserId = accessTokenClaims.getSubject();
		final String currentIpAddress = Helper.getClientIp(request);
		Assert.notNull(resolvedUserId, "user id may not be null");

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

		// TODO : refresh token 예외에따른 response 리턴 방식 변경
		// Refresh Token 검증. 실패시 로그아웃 상태이다.
		if (!jwtTokenProvider.validateToken(refreshToken)) {
			return Response.fail("Refresh Token 정보가 유효하지 않습니다.", HttpStatus.NOT_ACCEPTABLE);
		}
		if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
			return Response.fail("Refresh Token 이 아닙니다.", HttpStatus.NOT_ACCEPTABLE);
		}

		// Refresh token 정보 가져오기.
		Optional<RefreshToken> savedRefreshToken = refreshTokenRepository.findById(resolvedUserId);
		if (!savedRefreshToken.isPresent()) {
			return Response.fail("Refresh Token 정보와 Access Token 정보가 일치하지 않습니다.", HttpStatus.NOT_ACCEPTABLE);
		}
		// 최초 로그인한 ip 와 같은지 확인. (처리 방식에 따라 재발급을 하지 않거나 메일 등의 알림을 주는 방법이 있음)
		if (!savedRefreshToken.get().getIp().equals(currentIpAddress)) {
			refreshTokenRepository.delete(savedRefreshToken.get());
			return Response.fail("IP 주소가 다릅니다.", HttpStatus.NOT_ACCEPTABLE);
		}

		TokenResolverCommand.TokenInfo renewedTokenInfo
			= jwtTokenProvider.generateToken(resolvedUserId, savedRefreshToken.get().getAuthorities());// TODO: reissue: 토큰 재 생성방식

		// Redis RefreshToken update
		refreshTokenRepository.save(RefreshToken.builder()
			.userId(resolvedUserId)
			.ip(currentIpAddress)
			.authorities(savedRefreshToken.get().getAuthorities())
			.refreshToken(renewedTokenInfo.getRefreshToken())
			.build());

		return Response.success(renewedTokenInfo, "Token has been updated.", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
		final String accessToken = jwtTokenProvider.resolveAccessToken(request); //TODO: logout: accessToken 검증
		final String refreshToken = jwtTokenProvider.resolveRefreshToken(request); //TODO: logout: 예외처리
		Assert.notNull(accessToken, "access token may not be null");
		Assert.notNull(refreshToken, "refresh token may not be null");
		final String currentIpAddress = Helper.getClientIp(request);

		/*
		 *	검증목록 : access token 과 refresh token 의 소유자 일치, 동일 ip
		 *
		 */

		// Access Token 에서 User id 를 가져옴.
		String userId = jwtTokenProvider.getAuthentication(accessToken).getName();
		log.debug("### {} user id from authentication : [{}]", this.getClass().getName(), userId);

		// user id 로 refresh token repository 검색
		Optional<RefreshToken> refreshTokenInfo = refreshTokenRepository.findById(userId);
		if (!refreshTokenInfo.isPresent()) {
			return Response.fail("잘못된 요청입니다.", HttpStatus.UNAUTHORIZED);
		}
		refreshTokenRepository.delete(refreshTokenInfo.get());

		// 해당 Access Token 유효시간 가지고 와서 BlackList 로 저장하기
		Long expiration = jwtTokenProvider.getExpiration(accessToken);
		if (expiration > 0) {
			blacklistTokenRepository.save(BlacklistToken.builder()
				.accessToken(accessToken)
				.expiration(expiration).build());
		}
		return Response.success("로그아웃 되었습니다.");
	}

	// test
	public ResponseEntity<?> getAuthorities() {
		// SecurityContext에 담겨 있는 authentication id 정보
		String userId = securityUtil.getCurrentUser();
		User currentUser = userFinder.find(userId).orElseThrow(() -> new UsernameNotFoundException("사용자의 인증정보 없음."));
		return Response.success(currentUser.getRole().getCode(), "User role", HttpStatus.OK);
	}

	private void sendWelcomeMessage(User user) {
		mailManager.send(
			user.getDefaultEmail(),
			"Welcome to GitEditor",
			"welcome.ftl",
			MessageVariable.from("user", user)
		);
	}
}
