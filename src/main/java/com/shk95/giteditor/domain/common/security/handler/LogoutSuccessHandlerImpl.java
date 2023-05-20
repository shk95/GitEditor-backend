package com.shk95.giteditor.domain.common.security.handler;

import com.shk95.giteditor.domain.common.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

	private final JwtTokenProvider tokenProvider;

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
		throws IOException, ServletException {
		String username = tokenProvider.getAuthentication(tokenProvider.resolveAccessToken(request)).getName();
		log.info("logout success. user : [{}]", username);
	}
}
