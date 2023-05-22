package com.shk95.giteditor.domain.common.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class GeneratedJwtToken {
	private String grantType;
	private String accessToken;
	private String refreshToken;
}
