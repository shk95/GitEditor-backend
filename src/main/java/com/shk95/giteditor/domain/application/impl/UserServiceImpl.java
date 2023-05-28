package com.shk95.giteditor.domain.application.impl;

import com.shk95.giteditor.domain.application.UserService;
import com.shk95.giteditor.domain.application.commands.LoginCommand;
import com.shk95.giteditor.domain.application.commands.ReissueCommand;
import com.shk95.giteditor.domain.application.commands.SignupOAuthCommand;
import com.shk95.giteditor.domain.common.constant.ProviderType;
import com.shk95.giteditor.domain.common.mail.MailManager;
import com.shk95.giteditor.domain.common.mail.MessageVariable;
import com.shk95.giteditor.domain.common.security.Role;
import com.shk95.giteditor.domain.common.security.jwt.GeneratedJwtToken;
import com.shk95.giteditor.domain.common.security.jwt.JwtTokenProvider;
import com.shk95.giteditor.domain.model.provider.Provider;
import com.shk95.giteditor.domain.model.provider.ProviderId;
import com.shk95.giteditor.domain.model.provider.ProviderRepository;
import com.shk95.giteditor.domain.model.token.BlacklistToken;
import com.shk95.giteditor.domain.model.token.BlacklistTokenRepository;
import com.shk95.giteditor.domain.model.token.RefreshToken;
import com.shk95.giteditor.domain.model.token.RefreshTokenRepository;
import com.shk95.giteditor.domain.model.user.*;
import com.shk95.giteditor.utils.Helper;
import com.shk95.giteditor.utils.Response;
import com.shk95.giteditor.web.apis.request.AuthRequest;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.shk95.giteditor.config.ConstantFields.Jwt.AUTHORITIES_KEY;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final GrantedUserInfo grantedUserInfo;
	private final UserRepository userRepository;
	private final ProviderRepository providerRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	private final BlacklistTokenRepository blacklistTokenRepository;
	private final MailManager mailManager;


	@Override
	@Transactional
	public ResponseEntity<?> signupDefault(AuthRequest.Signup.Default signUp) {
		if (userRepository.existsByDefaultEmail(signUp.getDefaultEmail())) {
			return Response.fail("이미 회원가입된 이메일 입니다.", HttpStatus.BAD_REQUEST);
		}
		if (userRepository.existsById(new UserId(ProviderType.LOCAL, signUp.getUserId()))) {
			return Response.fail("이미 회원가입된 아이디 입니다.", HttpStatus.BAD_REQUEST);
		}
		userRepository.save(User.builder()
			.userId(new UserId(ProviderType.LOCAL, signUp.getUserId()))
			.password(passwordEncoder.encode(signUp.getPassword()))
			.defaultEmail(signUp.getDefaultEmail())
			.role(Role.USER)
			.username(signUp.getUsername())
			.build());
//		sendWelcomeMessage(user);
		return Response.success("회원가입에 성공했습니다.");
	}

	@Override
	@Transactional
	public Provider saveOAuthUser(SignupOAuthCommand command) {
		User user = User.builder().
			userId(new UserId(command.getOAuthUserProviderType(), command.getDefaultUserId()))
			.username(command.getDefaultUsername())
			.isUserEmailVerified(false)
			.isUserEnabled(true)
			.role(Role.USER)
			.profileImageUrl(command.getOAuthUserImgUrl())
			.build();
		User savedUser = userRepository.saveAndFlush(user);
		ProviderId providerId = new ProviderId(command.getOAuthUserProviderType(), command.getOAuthUserId());
		Provider providerUser = Provider.builder()
			.providerId(providerId)
			.providerUserName(command.getOAuthUserName())
			.providerEmail(command.getOAuthUserEmail())
			.providerLoginId(command.getOAuthUserLoginId())
			.providerImgUrl(command.getOAuthUserImgUrl())
			.user(savedUser)
			.build();
		return providerRepository.save(providerUser);
	}

	@Override
	public GeneratedJwtToken loginDefault(LoginCommand login, String ip) {
		// 로그인 인증정보 가져옴.
		CustomUserDetails userDetails = (CustomUserDetails) grantedUserInfo.loadUserByUsername(login.getUserId());//TODO: 예외처리
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
		GeneratedJwtToken tokenInfo = jwtTokenProvider.generateToken(authentication);

		// Redis RefreshToken 저장
		refreshTokenRepository.save(RefreshToken.builder()// TODO : redis transaction 설정
			.subject(jwtTokenProvider.getClaims(tokenInfo.getAccessToken()).getSubject())
			.ip(ip)
			.authorities(authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
			.refreshToken(tokenInfo.getRefreshToken())
			.build());
		return tokenInfo;
	}

	@Override
	public GeneratedJwtToken reissue(ReissueCommand command) {
		final String accessToken = command.getAccessToken();
		final String refreshToken = command.getRefreshToken();
		final String currentIpAddress = command.getIp();
		Assert.notNull(accessToken, "access token may not be null");
		Assert.notNull(refreshToken, "refresh token may not be null");

		final Claims claims = jwtTokenProvider.getClaims(accessToken);
		final String subject = claims.getSubject();
		Assert.notNull(subject, "Claim`s subject may not be null");

		Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
			.map(SimpleGrantedAuthority::new).collect(Collectors.toList());
		GeneratedJwtToken renewedTokenInfo = jwtTokenProvider
			.generateToken(ProviderType.valueOf(subject.split(",")[0]), subject.split(",")[1], authorities);

		// Redis RefreshToken update
		refreshTokenRepository.save(RefreshToken.builder()
			.subject(subject)
			.ip(currentIpAddress)
			.authorities(claims.get(AUTHORITIES_KEY).toString())
			.refreshToken(renewedTokenInfo.getRefreshToken())
			.build());

		return renewedTokenInfo;
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


	private void sendWelcomeMessage(User user) {
		mailManager.send(
			user.getDefaultEmail(),
			"Welcome to GitEditor",
			"welcome.ftl",
			MessageVariable.from("user", user)
		);
	}
}
