package com.shk95.giteditor.web.apis.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

public class ChatRequest {

	@Getter
	public static class Simple {

		@NotBlank
		private String prompt;
	}

	@Getter
	public static class Completion {

		@NotBlank(message = "페이지 번호는 필수 입력값 입니다.")
		private int pageAt;
		@NotBlank(message = "페이지 사이즈는 필수 입력값 입니다.")
		private int size;
	}
}
