package com.shk95.giteditor.web.apis.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

public class ChatRequest {

	@Getter
	public static class Simple {

		@NotBlank
		private String prompt;
	}
}
