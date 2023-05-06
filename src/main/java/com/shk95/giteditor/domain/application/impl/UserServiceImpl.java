package com.shk95.giteditor.domain.application.impl;

import com.shk95.giteditor.domain.application.UserService;
import com.shk95.giteditor.domain.common.mail.MailManager;
import com.shk95.giteditor.domain.common.mail.MessageVariable;
import com.shk95.giteditor.domain.common.security.UserDetailsImpl;
import com.shk95.giteditor.domain.common.security.jwt.JwtTokenProvider;
import com.shk95.giteditor.domain.model.roles.Authority;
import com.shk95.giteditor.domain.model.user.User;
import com.shk95.giteditor.domain.model.user.UserRepository;
import com.shk95.giteditor.infrastructure.repository.redis.RefreshToken;
import com.shk95.giteditor.infrastructure.repository.redis.RefreshTokenRedisRepository;
import com.shk95.giteditor.utils.Helper;
import com.shk95.giteditor.utils.Response;
import com.shk95.giteditor.utils.SecurityUtil;
import com.shk95.giteditor.web.payload.request.UserRequestDto;
import com.shk95.giteditor.web.payload.response.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final RefreshTokenRedisRepository refreshTokenRedisRepository;
	private final RedisTemplate redisTemplate;
	private final SecurityUtil securityUtil;
	private final MailManager mailManager;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		return userRepository.findByUsername(username).map(this::createUserDetails)
			.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
	}

	private UserDetails createUserDetails(User user) {
		return UserDetailsImpl.builder().userId(user.getUsername()).password(user.getPassword()).roles(user.getRoles()).build();
	}

	public ResponseEntity<?> signUp(UserRequestDto.SignUp signUp) {
		if (userRepository.existsByEmailAddress(signUp.getEmailAddress())) {
			return Response.fail("이미 회원가입된 이메일 입니다.", HttpStatus.BAD_REQUEST);
		}
		if (userRepository.existsByUsername(signUp.getUsername())) {
			return Response.fail("이미 회원가입된 아이디 입니다.", HttpStatus.BAD_REQUEST);
		}

		User user = User.builder()
			.username(signUp.getUsername())
			.emailAddress(signUp.getEmailAddress())
			.password(passwordEncoder.encode(signUp.getPassword()))
			.roles(Collections.singletonList(Authority.ROLE_USER))
			.build();
		userRepository.save(user);

		return Response.success("회원가입에 성공했습니다.");
	}

	public ResponseEntity<?> login(UserRequestDto.Login login, HttpServletRequest request) {

		if (userRepository.findByUsername(login.getUsername()).orElse(null) == null) {
			return Response.fail("해당하는 유저가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
		}

		// 1. Login ID/PW 를 기반으로 Authentication 객체 생성
		// 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
		UsernamePasswordAuthenticationToken authenticationToken = login.toAuthentication();

		// 2. 실제 검증 (사용자 비밀번호 체크)이 이루어지는 부분
		// authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

		// 3. 인증 정보를 기반으로 JWT 토큰 생성
		UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

		// 4. Redis RefreshToken 저장
		refreshTokenRedisRepository.save(RefreshToken.builder()
			.id(authentication.getName())
			.ip(Helper.getClientIp(request))
			.authorities(authentication.getAuthorities())
			.refreshToken(tokenInfo.getRefreshToken())
			.build());

		return Response.success(tokenInfo, "로그인에 성공했습니다.", HttpStatus.OK);
	}

	public ResponseEntity<?> reissue(UserRequestDto.Reissue reissue, HttpServletRequest request) {

		String accessToken = jwtTokenProvider.resolveToken(request);
		String refreshToken = reissue.getRefreshToken();
		String parseUsername = jwtTokenProvider.getAuthentication(accessToken).getName();

		// 1. Refresh Token 검증. 실패시 로그아웃 상태이다.
		if (!jwtTokenProvider.validateToken(refreshToken)) {
			// refresh token 만료(로그아웃 처리)
			return Response.fail("Refresh Token 정보가 유효하지 않습니다.", HttpStatus.BAD_REQUEST);
		}
		if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
			return Response.fail("Refresh Token 이 아닙니다.", HttpStatus.BAD_REQUEST);
		}
		//refresh token
		RefreshToken refreshTokenOld = refreshTokenRedisRepository.findByRefreshToken(refreshToken);
		if (!Objects.equals(refreshTokenOld.getId(), parseUsername)) {
			return Response.fail("Refresh Token 정보와 Access Token 정보가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
		}
		// 최초 로그인한 ip 와 같은지 확인 (처리 방식에 따라 재발급을 하지 않거나 메일 등의 알림을 주는 방법이 있음)
		String currentIpAddress = Helper.getClientIp(request);
		if (!refreshTokenOld.getIp().equals(currentIpAddress)) {
			return Response.fail("IP 주소가 다릅니다.", HttpStatus.BAD_REQUEST);
		}
		// Redis 에 저장된 RefreshToken 정보를 기반으로 JWT Token 생성
		UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(parseUsername, refreshTokenOld.getAuthorities());

		// Redis RefreshToken update
		refreshTokenRedisRepository.save(RefreshToken.builder()
			.id(parseUsername)
			.ip(currentIpAddress)
			.authorities(refreshTokenOld.getAuthorities())
			.refreshToken(tokenInfo.getRefreshToken())
			.build());

		return Response.success(tokenInfo, "Token 정보가 갱신되었습니다.", HttpStatus.OK);
	}

	public ResponseEntity<?> logout(UserRequestDto.Logout logout) {
		// 1. Access Token 검증
		if (!jwtTokenProvider.validateToken(logout.getAccessToken())) {
			return Response.fail("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);
		}

		// 2. Access Token 에서 User id 를 가져옵니다.
		Authentication authentication = jwtTokenProvider.getAuthentication(logout.getAccessToken());

		// 3. Redis 에서 해당 User id 로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
		refreshTokenRedisRepository.deleteRefreshTokenById(authentication.getName());

		// 4. 해당 Access Token 유효시간 가지고 와서 BlackList 로 저장하기
		Long expiration = jwtTokenProvider.getExpiration(logout.getAccessToken());

		redisTemplate.opsForValue()
			.set(logout.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);

		return Response.success("로그아웃 되었습니다.");
	}

	// test
	public ResponseEntity<?> authority() {
		// SecurityContext에 담겨 있는 authentication id 정보
		String userEmail = securityUtil.getCurrentUser();

		User user = userRepository.findByUsername(userEmail)
			.orElseThrow(() -> new UsernameNotFoundException("No authentication information."));

		// add ROLE_ADMIN
		user.getRoles().add(Authority.ROLE_ADMIN);
		userRepository.save(user);

		return Response.success();
	}

	private void sendWelcomeMessage(User user) {
		mailManager.send(
			user.getEmailAddress(),
			"Welcome to TaskAgile",
			"welcome.ftl",
			MessageVariable.from("user", user)
		);
	}
}
