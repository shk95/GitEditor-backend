package com.shk95.giteditor.domain.common.security.exception;

import com.shk95.giteditor.domain.common.security.info.OAuth2UserInfo;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class OAuthUserNotRegisteredException extends AuthenticationException {
	private final OAuth2UserInfo oAuth2UserInfo;

	public OAuthUserNotRegisteredException(String msg, Throwable cause, OAuth2UserInfo oAuth2UserInfo) {
		super(msg, cause);
		this.oAuth2UserInfo = oAuth2UserInfo;
	}
}
