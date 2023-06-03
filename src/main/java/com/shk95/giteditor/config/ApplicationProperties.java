package com.shk95.giteditor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Setter
@Getter
@Validated
@ConfigurationProperties(prefix = "app")
public final class ApplicationProperties {

	@NotEmpty
	private String tokenSecretKey;
	@NotEmpty
	private String mailFrom;
	@NotEmpty
	private String frontPageUrl;

	private FileStorage fileStorage;
	private Cors cors;
	private Image image;
	private Cdn cdn;

	@Setter
	@Getter
	public static class FileStorage {
		@NotEmpty
		private String localRootFolder;
		@NotEmpty
		private String tempFolder;
		@NotEmpty
		private String active;
		@NotEmpty
		private String s3AccessKey;
		@NotEmpty
		private String s3SecretKey;
		@NotEmpty
		private String s3BucketName;
		@NotEmpty
		private String s3Region;
	}

	@Setter
	@Getter
	public static class Image {
		private String commandSearchPath;
	}

	@Setter
	@Getter
	public static class Cdn {
		private String url;
	}

	@Setter
	@Getter
	public static class Cors {
		@NotBlank
		private String addMapping;
		@NotBlank
		private String allowedOrigins;
		private List<String> allowedMethods;
		private List<String> allowedHeaders;
		@NotNull
		private Long maxAge;
	}
}
