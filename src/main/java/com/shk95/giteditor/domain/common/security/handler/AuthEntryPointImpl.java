package com.shk95.giteditor.domain.common.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shk95.giteditor.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class AuthEntryPointImpl implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
						 AuthenticationException authException)
		throws IOException, ServletException {
		log.info("Unauthorized request : [{}], request uri : [{}]", authException.getMessage(), request.getRequestURI());

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpServletResponse.SC_OK);
		response.setCharacterEncoding("utf-8");

		final Map<String, Object> body = new HashMap<>();
		body.put("path", request.getServletPath());

		final ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(response.getOutputStream(), Response.fail(body, authException.getMessage(), HttpStatus.UNAUTHORIZED).getBody());
	}
}
