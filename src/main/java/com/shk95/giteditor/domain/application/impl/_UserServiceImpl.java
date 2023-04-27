package com.shk95.giteditor.domain.application.impl;

import com.shk95.giteditor.domain.application.UserService;
import com.shk95.giteditor.domain.common.mail.MailManager;
import com.shk95.giteditor.domain.common.mail.MessageVariable;
import com.shk95.giteditor.domain.common.security.jwt.JwtTokenProvider;
import com.shk95.giteditor.domain.model.user.RegistrationManagement;
import com.shk95.giteditor.domain.model.user.User;
import com.shk95.giteditor.domain.model.user.UserRepository;
import com.shk95.giteditor.infrastructure.repository.redis.RefreshToken;
import com.shk95.giteditor.infrastructure.repository.redis.RefreshTokenRedisRepository;
import com.shk95.giteditor.utils.Helper;
import com.shk95.giteditor.web.payload.request.UserRequestDto;
import com.shk95.giteditor.web.payload.response.ApiResponse;
import com.shk95.giteditor.web.payload.response.UserResponseDto;
import com.shk95.giteditor.web.results.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

@Deprecated
@Service
@Transactional
@RequiredArgsConstructor
public class _UserServiceImpl implements UserService {

	// jwt new
	private final RefreshTokenRedisRepository refreshTokenRedisRepository;
	private final ApiResponse response;
	private final JwtTokenProvider jwtTokenProvider;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final MailManager mailManager;
	private final UserRepository userRepository;
	private RegistrationManagement registrationManagement;


	/*public UserServiceImpl(RegistrationManagement registrationManagement,
	                       MailManager mailManager,
	                       CustomUserRepository customUserRepository) {
		this.registrationManagement = registrationManagement;
		this.mailManager = mailManager;
		this.customUserRepository = customUserRepository;
	}*/

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (StringUtils.isEmpty(username)) {
			throw new UsernameNotFoundException("No user found");
		}
		User user;
		if (username.contains("@")) {
			user = userRepository.findByEmailAddress(username);
		} else {
			user = userRepository.findByUsername(username).get();
		}
		if (user == null) {
			throw new UsernameNotFoundException("No user found by `" + username + "`");
		}
		return user;
	}


/*	@Override
	public User findById(Long userId) {
		return null;
	}

	@Override
	public void register(RegisterCommand command) throws RegistrationException {
		Assert.notNull(command, "Parameter `command` must not be null");
		User newUser = registrationManagement.register(
			command.getUsername(),
			command.getEmailAddress(),
			command.getPassword());

		sendWelcomeMessage(newUser);
	}*/

	private void sendWelcomeMessage(User user) {
		mailManager.send(
			user.getEmailAddress(),
			"Welcome to TaskAgile",
			"welcome.ftl",
			MessageVariable.from("user", user)
		);
	}

	// jwt new
	public ResponseEntity<?> signIn(HttpServletRequest request, UserRequestDto.Login signIn) {
		// 1. email, password 기반으로 Authentication 객체 생성
		// 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
		UsernamePasswordAuthenticationToken authenticationToken = signIn.toAuthentication();

		// 2. 실제 검증 (사용자 비밀번호 확인)이 이루어지는 부분
		// authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

		// 3. 인증 정보를 기반으로 JWT Token 생성
		UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

		// 4. Redis RefreshToken 저장
		refreshTokenRedisRepository.save(RefreshToken.builder()
			.id(authentication.getName())
			.ip(Helper.getClientIp(request))
			.authorities(authentication.getAuthorities())
			.refreshToken(tokenInfo.getRefreshToken())
			.build());

		return response.success(tokenInfo);
	}

	public ResponseEntity<?> reissue(HttpServletRequest request) {
		//TODO:: 1, 2 는 JwtAuthenticationFilter 동작과 중복되는 부분, 때문에 jwt filter 에서 다른 key 값으로 refresh token 값을
		//넘겨주고 여기서 받아서 처리하는 방법도 적용해 볼 수 있을 듯

		//1. Request Header 에서 JWT Token 추출
		String token = jwtTokenProvider.resolveToken(request);

		//2. validateToken 메서드로 토큰 유효성 검사
		if (token != null && jwtTokenProvider.validateToken(token)) {
			//3. refresh token 인지 확인
			if (jwtTokenProvider.isRefreshToken(token)) {
				//refresh token
				RefreshToken refreshToken = refreshTokenRedisRepository.findByRefreshToken(token);
				if (refreshToken != null) {
					//4. 최초 로그인한 ip 와 같은지 확인 (처리 방식에 따라 재발급을 하지 않거나 메일 등의 알림을 주는 방법이 있음)
					String currentIpAddress = Helper.getClientIp(request);
					if (refreshToken.getIp().equals(currentIpAddress)) {
						// 5. Redis 에 저장된 RefreshToken 정보를 기반으로 JWT Token 생성
						UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(refreshToken.getId(), refreshToken.getAuthorities());

						// 4. Redis RefreshToken update
						refreshTokenRedisRepository.save(RefreshToken.builder()
							.id(refreshToken.getId())
							.ip(currentIpAddress)
							.authorities(refreshToken.getAuthorities())
							.refreshToken(tokenInfo.getRefreshToken())
							.build());

						return response.success(tokenInfo);
					}
				}
			}
		}

		return response.fail("토큰 갱신에 실패했습니다.");
	}

	public ResponseEntity<?> signup(User user) {
		userRepository.save(user);

		return Result.created();
	}
}
