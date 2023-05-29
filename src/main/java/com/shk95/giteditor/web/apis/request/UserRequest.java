package com.shk95.giteditor.web.apis.request;

import lombok.Getter;

public class UserRequest {
	@Getter
	public static class Management {
		private String password;
		private String defaultEmail;
	}
}
