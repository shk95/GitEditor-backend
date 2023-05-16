package com.shk95.giteditor.domain.common.security.filter;

import com.shk95.giteditor.domain.common.security.jwt.JwtTokenProvider;
import com.shk95.giteditor.domain.model.token.BlacklistTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	//TODO: 필터 제외목록 점검. 필요한지?
	private static final List<String> EXCLUDE_URL = Collections.unmodifiableList(Arrays.asList(
		"/static/**",
		"/auth/signup"
	));
	private final JwtTokenProvider jwtTokenProvider;
	private final BlacklistTokenRepository blacklistTokenRepository;

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
		if (!token.isEmpty() && jwtTokenProvider.validateToken(token)) {
			// Redis 에 해당 accessToken logout 여부 확인
			if (!blacklistTokenRepository.findById(token).isPresent()) {
				// 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
				Authentication authentication = jwtTokenProvider.getAuthentication(token);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		filterChain.doFilter(request, response);
	}
}
