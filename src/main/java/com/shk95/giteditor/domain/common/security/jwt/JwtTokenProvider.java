package com.shk95.giteditor.domain.common.security.jwt;

import com.shk95.giteditor.domain.common.constant.ProviderType;
import com.shk95.giteditor.domain.model.user.CustomUserDetails;
import com.shk95.giteditor.domain.common.exception.TokenValidFailedException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import static com.shk95.giteditor.config.ConstantFields.Jwt.*;

@Slf4j
@Component
public class JwtTokenProvider {

	private final Key key;

	//The specified key byte array is 248 bits which is not secure enough for any JWT HMAC-SHA algorithm.
	// The JWT JWA Specification (RFC 7518, Section 3.2) states that keys used with HMAC-SHA algorithms MUST have a size >= 256 bits (the key size must be greater than or equal to the hash output size).
	// Consider using the io.jsonwebtoken.security.Keys#secretKeyFor(SignatureAlgorithm) method to create a key guaranteed to be secure enough for your preferred HMAC-SHA algorithm.
	private JwtTokenProvider(@Value("${app.token-secret-key}") String secretKey) {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(keyBytes);
		log.debug("jwt secret key : [{}]", secretKey);
	}

	//Authentication 을 가지고 AccessToken, RefreshToken 을 생성하는 메서드
	public GeneratedJwtToken generateToken(Authentication authentication) {

		return this.generateToken(((CustomUserDetails) authentication.getPrincipal()).getProviderType(), authentication.getName(), authentication.getAuthorities());
	}

	//name, authorities 를 가지고 AccessToken, RefreshToken 을 생성하는 메서드
	public GeneratedJwtToken generateToken(ProviderType providerType, String userId, Collection<? extends GrantedAuthority> inputAuthorities) {
		Date now = new Date();
		//권한 가져오기
		String authorities = inputAuthorities.stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		String subject = providerType.name() + ',' + userId;

		//Generate AccessToken
		String accessToken = Jwts.builder()
			.setSubject(subject)
			.claim(AUTHORITIES_KEY, authorities)
			.claim("type", TYPE_ACCESS)
			.setIssuedAt(now)   //토큰 발행 시간 정보
			.setExpiration(new Date(now.getTime() + ExpireTime.ACCESS_TOKEN_EXPIRE_TIME))  //토큰 만료 시간 설정
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();

		//Generate RefreshToken
		String refreshToken = Jwts.builder()
			.claim("type", TYPE_REFRESH)
			.setIssuedAt(now)   //토큰 발행 시간 정보
			.setExpiration(new Date(now.getTime() + ExpireTime.REFRESH_TOKEN_EXPIRE_TIME)) //토큰 만료 시간 설정
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();

		return GeneratedJwtToken.builder()
			.grantType(BEARER_TYPE)
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	// 사용자 인증을 위한 내부 인증용 토큰을 생성
	public Authentication getAuthentication(String accessToken) {
		Claims claims = getClaims(accessToken);
		if (claims.get(AUTHORITIES_KEY) == null) {
			throw new TokenValidFailedException("권한 정보가 없는 토큰입니다.");
		}
		//클레임에서 권한 정보 가져오기
		Collection<? extends GrantedAuthority> authorities =
			Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());

		String[] subject = claims.getSubject().split(",");
		//UserDetails 객체를 만들어서 Authentication 리턴
		CustomUserDetails principal = CustomUserDetails.builder()// TODO : jwt 로그인 보완하기
			.userId(subject[1])
			.providerType(ProviderType.valueOf(subject[0]))
			.password("")
			.authorities(authorities).build();
		return new UsernamePasswordAuthenticationToken(principal, accessToken, authorities);
	}

	//토큰 유효성 검증
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			log.info("Invalid JWT Token", e);
		} catch (ExpiredJwtException e) {
			log.info("Expired JWT Token", e);
		} catch (UnsupportedJwtException e) {
			log.info("Unsupported JWT Token", e);
		} catch (IllegalArgumentException e) {
			log.info("JWT claims string is empty.", e);
		}
		return false;
	}

	public Claims getClaims(String accessToken) {
		try {
			return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
		} catch (ExpiredJwtException e) {
			// 만료된 토큰일경우
			return e.getClaims();
		} catch (JwtException e) {
			// 올바르지 않은 토큰
			// TODO : 올바르지 않은 access token 에 대한 처리
			return null;
		}
	}

	public boolean isRefreshToken(String token) {
		String type = (String) Jwts.parserBuilder().setSigningKey(key).build()
			.parseClaimsJws(token).getBody().get("type");
		return type.equals(TYPE_REFRESH);
	}

	public Long getExpiration(String accessToken) {
		//accessToken 남은 유효시간
		Date expiration = Jwts.parserBuilder().setSigningKey(key).build()
			.parseClaimsJws(accessToken).getBody().getExpiration();
		return (expiration.getTime() - System.currentTimeMillis());
	}

	// bearer 토큰 타입 확인후 토큰값 리턴
	public String resolveAccessToken(HttpServletRequest request) {
		String tokenCollectedFromHeader = request.getHeader(AUTHORIZATION_HEADER);
		return StringUtils.hasText(tokenCollectedFromHeader) && tokenCollectedFromHeader.startsWith(BEARER_TYPE)
			? tokenCollectedFromHeader.substring(7) : "";
	}

	public String resolveRefreshToken(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		return Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(TYPE_REFRESH))
			.map(Cookie::getValue).findFirst().orElse("");
	}
}
