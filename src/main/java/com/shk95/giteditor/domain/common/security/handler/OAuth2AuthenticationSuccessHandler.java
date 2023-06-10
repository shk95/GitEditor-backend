package com.shk95.giteditor.domain.common.security.handler;

import com.shk95.giteditor.config.ApplicationProperties;
import com.shk95.giteditor.domain.common.security.jwt.GeneratedJwtToken;
import com.shk95.giteditor.domain.common.security.jwt.JwtTokenProvider;
import com.shk95.giteditor.domain.common.security.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.shk95.giteditor.domain.model.token.RefreshToken;
import com.shk95.giteditor.domain.model.token.RefreshTokenRepository;
import com.shk95.giteditor.domain.model.user.CustomUserDetails;
import com.shk95.giteditor.utils.CookieUtil;
import com.shk95.giteditor.utils.Helper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.shk95.giteditor.config.ConstantFields.Jwt.ExpireTime.REFRESH_TOKEN_EXPIRE_TIME;
import static com.shk95.giteditor.config.ConstantFields.Jwt.JWT_TYPE_REFRESH;
import static com.shk95.giteditor.config.ConstantFields.OAuthRepo.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final ApplicationProperties properties;
	private final JwtTokenProvider tokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;

	// oAuth provider 로그인 페이지 로그인 성공후
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response
		, Authentication authentication) throws IOException, ServletException {

		final String targetUrl = this.determineTargetUrl(request, response, authentication);

		if (response.isCommitted()) {
			log.info("Response has already been committed. Unable to redirect to [{}].", targetUrl);
			return;
		}
		this.clearAuthenticationAttributes(request, response);
		super.getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}

	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		Optional<String> redirectUri = CookieUtil.getCookie(request, OAUTH_REDIRECT_URI_PARAM_COOKIE_NAME)
			.map(Cookie::getValue);

		if (redirectUri.isPresent() && !this.isAuthorizedRedirectUri(redirectUri.get())) {// oAuth 로그인 페이지에서 인증 성공후 반환된 리다이렉트
			throw new IllegalArgumentException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
		}

		final String TARGET_URL = redirectUri.orElse(getDefaultTargetUrl());

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		GeneratedJwtToken tokenInfo = tokenProvider.generateToken(authentication);

		log.info("{}. client ip : {}", getClass(), Helper.getClientIp(request));
		log.info("{}. request.getRemote() : {}", getClass(), request.getRemoteAddr());
		// Redis RefreshToken 저장
		refreshTokenRepository.save(RefreshToken.builder()
			.subject(tokenProvider.getClaims(tokenInfo.getAccessToken()).getSubject())
			.ip(Helper.getClientIp(request))
			.authorities(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
			.refreshToken(tokenInfo.getRefreshToken())
			.build());

		CookieUtil.deleteCookie(request, response, JWT_TYPE_REFRESH);
		CookieUtil.addCookie(response, JWT_TYPE_REFRESH, tokenInfo.getRefreshToken(), (int) (REFRESH_TOKEN_EXPIRE_TIME / 1000));

		return UriComponentsBuilder.fromUriString(TARGET_URL)
			.queryParam("token", tokenInfo.getAccessToken())
			.build().toUriString();
	}

	protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
		super.clearAuthenticationAttributes(request);
		authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
	}

	private boolean hasAuthority(Collection<? extends GrantedAuthority> authorities, String authority) {
		if (authorities == null) {
			return false;
		}

		for (GrantedAuthority grantedAuthority : authorities) {
			if (authority.equals(grantedAuthority.getAuthority())) {
				return true;
			}
		}
		return false;
	}

	private boolean isAuthorizedRedirectUri(String uri) {
		URI clientRedirectUri = URI.create(uri);
		URI authorizedURI = URI.create(properties.getFrontPageUrl() + OAUTH_DEFAULT_REDIRECT);
		// Only validate host and port. Let the clients use different paths if they want to
		return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
			&& authorizedURI.getPort() == clientRedirectUri.getPort();
	}
}
