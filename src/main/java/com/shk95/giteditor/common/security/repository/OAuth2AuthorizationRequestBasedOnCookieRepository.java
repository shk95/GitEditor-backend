package com.shk95.giteditor.common.security.repository;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.shk95.giteditor.common.utils.web.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import static com.shk95.giteditor.config.Constants.OAuthRepo.*;

@Component
public class OAuth2AuthorizationRequestBasedOnCookieRepository
	implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

	@Override
	public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
		return CookieUtil.getCookie(request, OAUTH_AUTHORIZATION_REQUEST_COOKIE_NAME)
			.map(cookie -> CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest.class))
			.orElse(null);
	}

	@Override
	public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request
		, HttpServletResponse response) {

		if (authorizationRequest == null) {
			CookieUtil.deleteCookie(request, response, OAUTH_AUTHORIZATION_REQUEST_COOKIE_NAME);
			CookieUtil.deleteCookie(request, response, OAUTH_REDIRECT_URI_PARAM_COOKIE_NAME);
			CookieUtil.deleteCookie(request, response, OAUTH_REFRESH_TOKEN);
			return;
		}

		CookieUtil.addCookie(response, OAUTH_AUTHORIZATION_REQUEST_COOKIE_NAME
			, CookieUtil.serialize(authorizationRequest), OAUTH_COOKIE_EXPIRE_SECONDS);
		String redirectUriAfterLogin = request.getParameter(OAUTH_REDIRECT_URI_PARAM_COOKIE_NAME);
		if (StringUtils.isNotBlank(redirectUriAfterLogin)) {
			CookieUtil.addCookie(response, OAUTH_REDIRECT_URI_PARAM_COOKIE_NAME, redirectUriAfterLogin, OAUTH_COOKIE_EXPIRE_SECONDS);
		}
	}

	/*@Override
	public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
		return this.loadAuthorizationRequest(request);
	}*/

	@Override
	public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request
		, HttpServletResponse response) {
		return this.loadAuthorizationRequest(request);
	}

	public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
		CookieUtil.deleteCookie(request, response, OAUTH_AUTHORIZATION_REQUEST_COOKIE_NAME);
		CookieUtil.deleteCookie(request, response, OAUTH_REDIRECT_URI_PARAM_COOKIE_NAME);
		CookieUtil.deleteCookie(request, response, OAUTH_REFRESH_TOKEN);
	}
}
