package com.shk95.giteditor.domain.application.impl;

import com.shk95.giteditor.config.ApplicationProperties;
import com.shk95.giteditor.domain.application.UserService;
import com.shk95.giteditor.domain.application.commands.*;
import com.shk95.giteditor.domain.common.constant.ProviderType;
import com.shk95.giteditor.domain.common.exception.TokenValidFailedException;
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
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.UUID;
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
	private final ApplicationProperties properties;

	@Override
	@Transactional
	public SignupResult signupDefault(SignupCommand.Default signUp) {
		if (userRepository.existsByDefaultEmail(signUp.getDefaultEmail())) {
			return SignupResult.fail().message("이미 회원가입된 이메일 입니다.").build();
		}
		if (userRepository.existsById(new UserId(ProviderType.LOCAL, signUp.getUserId()))) {
			return SignupResult.fail().message("이미 회원가입된 아이디 입니다.").build();
		}
		String emailCode = this.sendVerificationEmail(signUp.getDefaultEmail());
		User user = userRepository.save(User.builder()
			.userId(new UserId(ProviderType.LOCAL, signUp.getUserId()))
			.password(passwordEncoder.encode(signUp.getPassword()))
			.defaultEmail(signUp.getDefaultEmail())
			.role(Role.TEMP)
			.username(signUp.getUsername())
			.isUserEmailVerified(false)
			.isUserEnabled(true)
			.emailVerificationCode(emailCode)
			.build());
		return SignupResult.success().message("회원가입에 성공했습니다.").build();
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
			.isGithubEnabled(command.getOAuthUserProviderType()==ProviderType.GITHUB)
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
	public GeneratedJwtToken loginDefault(LoginCommand login) {
		// 로그인 인증정보 가져옴.
		CustomUserDetails userDetails = (CustomUserDetails) grantedUserInfo.loadUserByUsername(login.getUserId());
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
			.ip(login.getIp())
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
		GeneratedJwtToken renewedTokenInfo;// TODO: reissue: 토큰 재 생성방식
		try {
			renewedTokenInfo = jwtTokenProvider
				.generateToken(UserId.of(subject), authorities);
		} catch (Exception e) {
			throw new TokenValidFailedException();
		}

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
	public void logout(LogoutCommand command) {
		// user id 로 refresh token repository 검색
		refreshTokenRepository.findByRefreshToken(command.getRefreshToken())
			.filter((e) -> e.getIp().equals(command.getIp()))
			.ifPresent(refreshTokenRepository::delete);
		// 해당 Access Token 유효시간 가지고 와서 BlackList 로 저장하기
		Long expiration = jwtTokenProvider.getExpiration(command.getAccessToken());
		if (expiration > 0) {
			blacklistTokenRepository.save(BlacklistToken.builder()
				.accessToken(command.getAccessToken())
				.expiration(expiration).build());
		}
	}

	private String sendVerificationEmail(String email) {
		String code = Base64.getUrlEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
		String home = properties.getFrontPageUrl();
		mailManager.send(
			email,
			"Welcome to GitEditor",
			"welcome.ftl",
			MessageVariable.from("code", home + "/redirect?type=emailVerification&code=" + code),
			MessageVariable.from("message", "회원가입을 축하합니다. 아래의 링크를 클릭해서 계정을 활성화해 주세요.")
		);
		return code;
	}
}
