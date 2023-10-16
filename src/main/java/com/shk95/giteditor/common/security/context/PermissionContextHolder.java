package com.shk95.giteditor.common.security.context;

import com.shk95.giteditor.common.utils.string.Convert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class PermissionContextHolder {
	private static final String PERMISSION_CONTEXT_ATTRIBUTES = "PERMISSION_CONTEXT";

	public static String getContext() {
		return Convert.toStr(RequestContextHolder.currentRequestAttributes().getAttribute(PERMISSION_CONTEXT_ATTRIBUTES,
			RequestAttributes.SCOPE_REQUEST));
	}

	public static void setContext(String permission) {
		RequestContextHolder.currentRequestAttributes().setAttribute(PERMISSION_CONTEXT_ATTRIBUTES, permission,
			RequestAttributes.SCOPE_REQUEST);
	}
}
