package com.shk95.giteditor.core.openai.adapter.in;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class ChatRequest {

	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	public static class Simple {

		@NotBlank
		private String prompt;
	}
}
