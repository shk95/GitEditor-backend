package com.shk95.giteditor.core.user.adapter.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class SignupResult {
	private String message;
	private boolean status;

	private SignupResult() {
	}

	public static SignupResultBuilder success() {
		return SignupResult.builder().status(true);
	}

	public static SignupResultBuilder fail() {
		return SignupResult.builder().status(false);
	}
}
