package com.shk95.giteditor.domain.common.exception;

import com.shk95.giteditor.domain.common.model.AbstractOAuth2UserInfo;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class OAuthUserNotRegisteredException extends AuthenticationException {
	private final AbstractOAuth2UserInfo oAuth2UserInfo;

	public OAuthUserNotRegisteredException(String msg, Throwable cause, AbstractOAuth2UserInfo oAuth2UserInfo) {
		super(msg, cause);
		this.oAuth2UserInfo = oAuth2UserInfo;
	}
}
