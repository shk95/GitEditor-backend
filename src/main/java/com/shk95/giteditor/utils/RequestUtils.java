package com.shk95.giteditor.utils;

import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;

@Deprecated
@NoArgsConstructor
public final class RequestUtils {

	public static IpAddress getIpAddress(HttpServletRequest request) {
		Assert.notNull(request, "Parameter `request` must not be null");

		String remoteAddress = request.getRemoteAddr();
		String x;
		if ((x = request.getHeader("X-FORWARDED-FOR")) != null) {
			remoteAddress = x;
			int idx = remoteAddress.indexOf(',');
			if (idx > -1) {
				remoteAddress = remoteAddress.substring(0, idx);
			}
		}
		return new IpAddress(remoteAddress);
	}
}
