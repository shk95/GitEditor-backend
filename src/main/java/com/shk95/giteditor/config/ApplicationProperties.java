package com.shk95.giteditor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@Validated
@ConfigurationProperties(prefix = "app")
public class ApplicationProperties {

	private Cors cors;

	@Setter
	@Getter
	public static class Cors {
		@NotBlank
		private String allowedOrigins;
		@NotBlank
		private String allowedMethods;
		@NotBlank
		private String allowedHeaders;
		@NotNull
		private Long maxAge;
	}
}
