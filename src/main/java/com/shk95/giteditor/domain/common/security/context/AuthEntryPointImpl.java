package com.shk95.giteditor.domain.common.security.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shk95.giteditor.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AuthEntryPointImpl extends DefaultRedirectStrategy implements AuthenticationEntryPoint {

	public static final String REDIRECT_LOGIN_PATH = "/login";// TODO : 로그인 인증 리다이렉트 보완

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
						 AuthenticationException authException)
		throws IOException, ServletException {

		log.info("Unauthorized request uri : [{}] request : [{}]", authException.getMessage(), request.getRequestURI());

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("utf-8");

		final Map<String, Object> body = new HashMap<>();
		body.put("path", request.getServletPath());
		body.put("cause", "인증 실패. 로그인 필요");

		new ObjectMapper().writeValue(response.getOutputStream()
			, Response.fail(body, authException.getMessage(), HttpStatus.UNAUTHORIZED).getBody());

		sendRedirect(request, response, REDIRECT_LOGIN_PATH);
	}
}
