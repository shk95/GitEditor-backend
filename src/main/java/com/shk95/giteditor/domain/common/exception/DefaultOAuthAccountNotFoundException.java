package com.shk95.giteditor.domain.common.exception;

//TODO: 예외처리

import org.springframework.security.core.AuthenticationException;

//oAuth 로 가입한 사용자의 oAuth 계정정보를 찾을수 없을경우
public class DefaultOAuthAccountNotFoundException extends AuthenticationException {
	public DefaultOAuthAccountNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public DefaultOAuthAccountNotFoundException() {
		super("oAuth 가입 사용자의 정보를 찾을 수 없습니다.", null);
	}
}
