package com.shk95.giteditor.web.apis.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

public class ChatRequest {

	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	public static class Simple {

		@NotBlank
		private String prompt;
	}
}
