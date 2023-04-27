package com.shk95.giteditor.web.apis.handlers.authenticate;

import com.shk95.giteditor.utils.JsonUtils;
import com.shk95.giteditor.web.results.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SimpleLogoutSuccessHandler implements LogoutSuccessHandler {
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
		response.setStatus(HttpStatus.OK.value());
		JsonUtils.write(response.getWriter(), ApiResult.message("logged-out"));
	}
}
