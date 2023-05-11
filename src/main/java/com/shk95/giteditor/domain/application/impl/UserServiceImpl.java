package com.shk95.giteditor.domain.application.impl;

import com.shk95.giteditor.domain.application.UserService;
import com.shk95.giteditor.domain.common.mail.MailManager;
import com.shk95.giteditor.domain.common.mail.MessageVariable;
import com.shk95.giteditor.domain.common.security.UserDetailsImpl;
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
import com.shk95.giteditor.web.payload.request.UserRequestDto;
import com.shk95.giteditor.web.payload.response.TokenResolverCommand;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final AuthenticationManager authenticationManager;
	private final RefreshTokenRepository refreshTokenRepository;
	private final BlacklistTokenRepository blacklistTokenRepository;
	private final SecurityUtil securityUtil;
	private final MailManager mailManager;
	private final UserFinder userFinder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userFinder.find(username).orElse(null);
		if (user == null) {
			throw new UsernameNotFoundException("Cannot find user. user : [" + username + "]");
		}
		return UserDetailsImpl.createUserDetailsBuilder(user).build();
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
			.defaultEmail(signUp.getDefaultEmail())
			.password(passwordEncoder.encode(signUp.getPassword()))
			.username(signUp.getUsername())
			.build());
//		sendWelcomeMessage(user);
		return Response.success("회원가입에 성공했습니다.");
	}

	@Override
	public ResponseEntity<?> defaultLogin(UserRequestDto.Login login, HttpServletRequest request
		, HttpServletResponse response) {
		UserDetailsImpl userDetails = (UserDetailsImpl) this.loadUserByUsername(login.getUserId());//TODO: 예외처리
		UsernamePasswordAuthenticationToken authenticationToken
			= new UsernamePasswordAuthenticationToken(
			userDetails
			, login.getPassword()
			, userDetails.getAuthorities());
		Authentication authentication = authenticationManager.authenticate(authenticationToken);// 인증
		//SecurityContextHolder.getContext().setAuthentication(authentication); // not necessary

		// 인증 정보를 기반으로 JWT 토큰 생성
		TokenResolverCommand.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

		log.debug("in default login method. authentication's name : [{}]", authentication.getName());
		// 4. Redis RefreshToken 저장
		refreshTokenRepository.save(RefreshToken.builder()
			.id(authentication.getName())
			.ip(Helper.getClientIp(request))
			.authorities(authentication.getAuthorities())
			.refreshToken(tokenInfo.getRefreshToken())
			.build());
		return Response.success(tokenInfo, "로그인에 성공했습니다.", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
		String accessToken = jwtTokenProvider.resolveAccessToken(request);
		//TODO accessToken 검증
		//refreshToken 헤더에서 가져옴
		String refreshToken = jwtTokenProvider.resolveRefreshToken(request);
		// 예외처리
		Claims accessTokenClaims = jwtTokenProvider.getClaims(accessToken);

		String resolvedUserId = accessTokenClaims.getSubject();
		String currentIpAddress = Helper.getClientIp(request);

		Assert.notNull(accessToken, "access token may not be null");
		Assert.notNull(refreshToken, "refresh token may not be null");
		Assert.notNull(resolvedUserId, "parseUsername may not be null");

		/*
		 *	검증목록
		 *	1. access token claim key, type, subject, expiration
		 *	2. refresh token expiration, type
		 */

		// Refresh Token 검증. 실패시 로그아웃 상태이다.
		if (!jwtTokenProvider.validateToken(refreshToken)) {
			return Response.fail("Refresh Token 정보가 유효하지 않습니다.", HttpStatus.NOT_ACCEPTABLE);
		}
		if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
			return Response.fail("Refresh Token 이 아닙니다.", HttpStatus.NOT_ACCEPTABLE);
		}

		// Refresh token
		RefreshToken bySavedRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken);
		if (!Objects.equals(bySavedRefreshToken.getId(), resolvedUserId)) {
			return Response.fail("Refresh Token 정보와 Access Token 정보가 일치하지 않습니다.", HttpStatus.NOT_ACCEPTABLE);
		}
		// 최초 로그인한 ip 와 같은지 확인 (처리 방식에 따라 재발급을 하지 않거나 메일 등의 알림을 주는 방법이 있음)
		if (!bySavedRefreshToken.getIp().equals(currentIpAddress)) {
			return Response.fail("IP 주소가 다릅니다.", HttpStatus.NOT_ACCEPTABLE);
		}

		Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

		// Redis 에 저장된 RefreshToken 정보를 기반으로 JWT Token 생성
		TokenResolverCommand.TokenInfo renewedRefreshToken = jwtTokenProvider.generateToken(authentication);

		// Redis RefreshToken update
		refreshTokenRepository.save(RefreshToken.builder()
			.id(resolvedUserId)
			.ip(currentIpAddress)
			.refreshToken(renewedRefreshToken.getRefreshToken())
			.build());

		return Response.success(renewedRefreshToken, "Token has been updated.", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
		String accessToken = jwtTokenProvider.resolveAccessToken(request);
		//TODO accessToken 검증

		//refreshToken 헤더에서 가져옴
		String refreshToken = jwtTokenProvider.resolveRefreshToken(request);
		//TODO 널 처리

		/*
		 *	검증목록 : access token 과 refresh token 의 소유자 일치, 동일 ip
		 *
		 */
		// Access Token 에서 User id 를 가져옵니다.
		Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
		String userId = authentication.getName();
		log.debug("in logout method. userId from authentication : [{}]", userId);

		Optional<RefreshToken> refreshTokenInfo = refreshTokenRepository.findById(userId);
		if (!(refreshTokenInfo.isPresent() && userId.equals(refreshTokenInfo.get().getId()))) {
			return Response.fail("잘못된 요청입니다.", HttpStatus.UNAUTHORIZED);
		}
		// Redis 에서 해당 User id 로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
		refreshTokenRepository.deleteRefreshTokenByRefreshToken(refreshToken);

		// 해당 Access Token 유효시간 가지고 와서 BlackList 로 저장하기
		Long expiration = jwtTokenProvider.getExpiration(accessToken);
		if (expiration > 0) {
			blacklistTokenRepository.save(BlacklistToken.builder()
				.accessToken(accessToken)
				.isLogout(true)
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
