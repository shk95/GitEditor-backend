package com.shk95.giteditor.common.security.filter;

import com.shk95.giteditor.common.security.jwt.JwtTokenProvider;
import com.shk95.giteditor.core.auth.application.port.out.BlacklistTokenRepositoryPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final List<String> EXCLUDE_URL = List.of("/static/**", "/auth/signup");

	private final JwtTokenProvider jwtTokenProvider;
	private final BlacklistTokenRepositoryPort blacklistTokenRepositoryPort;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		return EXCLUDE_URL.stream().anyMatch(exclude -> exclude.equalsIgnoreCase(request.getServletPath()));
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		// 1. Request Header 에서 JWT 토큰 추출
		final String token = jwtTokenProvider.resolveAccessToken(request);

		// 2. validateToken 으로 토큰 유효성 검사
		if (!token.isEmpty() && jwtTokenProvider.isVerified(token)) {
			// Redis 에 해당 accessToken logout 여부 확인
			if (blacklistTokenRepositoryPort.findByAccessToken(token).isEmpty()) {
				// 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
				Authentication authentication = jwtTokenProvider.getAuthentication(token);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		filterChain.doFilter(request, response);
	}
}
