package com.shk95.giteditor.web.apis.authenticate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.shk95.giteditor.config.ConstantFields.REDIRECT_LOGIN_PATH;

@Slf4j
@Component
public class AuthEntryPointImpl extends DefaultRedirectStrategy implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
		throws IOException, ServletException {

		log.info("Unauthorized request. message : [{}] uri : [{}] request : [{}]"
			, authException.getMessage(), request.getRequestURI(), request.getServletPath());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("utf-8");

		sendRedirect(request, response, REDIRECT_LOGIN_PATH);
	}
}
