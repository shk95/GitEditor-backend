package com.shk95.giteditor.common.authenticate;

import com.shk95.giteditor.config.ApplicationProperties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.shk95.giteditor.config.Constants.REDIRECT_LOGIN_PATH;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthEntryPointImpl extends DefaultRedirectStrategy implements AuthenticationEntryPoint {

	private final ApplicationProperties properties;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
		throws IOException, ServletException {

		log.info("Unauthorized request. message.http : [{}] uri : [{}] request : [{}]"
			, authException.getMessage(), request.getRequestURI(), request.getServletPath());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("utf-8");

		sendRedirect(request, response, properties.getFrontPageUrl() + REDIRECT_LOGIN_PATH);
	}
}
