package com.shk95.giteditor.utils;

import javax.servlet.http.HttpServletRequest;

import static com.shk95.giteditor.config.ConstantFields.Jwt.AUTHORIZATION_HEADER;
import static com.shk95.giteditor.config.ConstantFields.Jwt.BEARER_TYPE;

public class HeaderUtil {

	public static String getAccessToken(HttpServletRequest request) {
		String headerValue = request.getHeader(AUTHORIZATION_HEADER);

		if (headerValue == null) {
			return null;
		}

		if (headerValue.startsWith(BEARER_TYPE)) {
			return headerValue.substring(BEARER_TYPE.length() + 1);
		}

		return null;
	}
}
