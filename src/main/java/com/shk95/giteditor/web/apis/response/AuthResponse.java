package com.shk95.giteditor.web.apis.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class AuthResponse {

	@AllArgsConstructor
	@Builder
	@Getter
	public static class Signup {
		private String message;

		private Signup() {
		}
	}
}
