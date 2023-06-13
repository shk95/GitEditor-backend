package com.shk95.giteditor.domain.common.constant;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum ProviderType implements Serializable {
	GOOGLE,
	FACEBOOK,
	NAVER,
	KAKAO,
	GITHUB,
	LOCAL,
	ANONYMOUS;
}
