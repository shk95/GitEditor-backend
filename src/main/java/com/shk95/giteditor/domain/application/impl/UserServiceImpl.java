package com.shk95.giteditor.domain.application.impl;

import com.shk95.giteditor.domain.application.UserService;
import com.shk95.giteditor.domain.common.security.UserDetailsImpl;
import com.shk95.giteditor.domain.common.security.jwt.JwtTokenProvider;
import com.shk95.giteditor.domain.model.roles.Authority;
import com.shk95.giteditor.domain.model.user.User;
import com.shk95.giteditor.domain.model.user.UserRepository;
import com.shk95.giteditor.utils.SecurityUtil;
import com.shk95.giteditor.web.payload.request.UserRequestDto;
import com.shk95.giteditor.utils.Response;
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
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final RedisTemplate redisTemplate;
	private final SecurityUtil securityUtil;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		return userRepository.findByUsername(username).map(this::createUserDetails)
			.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
	}

	private UserDetails createUserDetails(User user) {
		return UserDetailsImpl.builder().username(user.getUsername()).password(user.getPassword()).roles(user.getRoles()).build();
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

	public ResponseEntity<?> login(UserRequestDto.Login login) {

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

		// 4. RefreshToken Redis 저장 (expirationTime 설정을 통해 자동 삭제 처리)
		redisTemplate.opsForValue()
			.set("RT:" + authentication.getName(), tokenInfo.getRefreshToken()
				, tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

		return Response.success(tokenInfo, "로그인에 성공했습니다.", HttpStatus.OK);
	}

	public ResponseEntity<?> reissue(UserRequestDto.Reissue reissue) {
		// 1. Refresh Token 검증. 실패시 로그아웃 상태이다.
		if (!jwtTokenProvider.validateToken(reissue.getRefreshToken())) {
			// refresh token 만료(로그아웃 처리)
			return Response.fail("Refresh Token 정보가 유효하지 않습니다.", HttpStatus.BAD_REQUEST);
		}

		// 2. Access Token 에서 User id 를 가져옴.
		Authentication authentication = jwtTokenProvider.getAuthentication(reissue.getAccessToken());
		// 3. Redis 에서 User id 로 저장된 Refresh Token 값을 가져옴
		String refreshToken = (String) redisTemplate.opsForValue().get("RT:" + authentication.getName());

		// 로그아웃되어 Redis 에 RefreshToken 이 존재하지 않는 경우.
		if (ObjectUtils.isEmpty(refreshToken)) {
			// refresh token 만료(로그아웃 처리)
			return Response.fail("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);
		}
		if (!refreshToken.equals(reissue.getRefreshToken())) {
			// refresh token 소유자(access token) 불일치
			return Response.fail("Refresh Token 정보가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
		}

		// 4. 새로운 토큰 생성
		UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

		// 5. RefreshToken Redis 업데이트
		redisTemplate.opsForValue()
			.set("RT:" + authentication.getName(), tokenInfo.getRefreshToken()
				, tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

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
		if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
			// Refresh Token 삭제
			redisTemplate.delete("RT:" + authentication.getName());
		}

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
}
