/*
package com.shk95.giteditor.domain.common.security.jwt;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {

	private TokenBlacklistService tokenBlacklistService;

	public JwtLogoutSuccessHandler(TokenBlacklistService tokenBlacklistService) {
		this.tokenBlacklistService = tokenBlacklistService;
	}

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		String token = request.getHeader("Authorization").replace("Bearer ", "");
		if (token != null && !tokenBlacklistService.isTokenBlacklisted(token)) {
			tokenBlacklistService.addTokenToBlacklist(token);
			response.setStatus(HttpStatus.OK.value());
			response.getWriter().write("Logged out successfully");
			response.getWriter().flush();
			response.getWriter().close();
		} else {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.getWriter().write("Token is already blacklisted or missing");
			response.getWriter().flush();
			response.getWriter().close();
		}
	}
}
*/
