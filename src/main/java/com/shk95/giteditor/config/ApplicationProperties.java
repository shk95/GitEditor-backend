package com.shk95.giteditor.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

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
	private CacheConfig cache;
	private Discord discord;

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

	@Setter
	@Getter
	public static class CacheConfig {

		public Cache defaultCache;
		public List<Cache> names;

		@Setter
		@Getter
		public static class Cache {

			private String name;
			private Long maxEntriesLocalHeap;
			private Long timeToIdleSeconds;
			private Long timeToLiveSeconds;
			private Boolean eternal;
			private Long diskExpiryThreadIntervalSeconds;
			private String memoryStoreEvictionPolicy;
		}
	}

	@Setter
	@Getter
	public static class Discord {

		public Bot bot;

		@Setter
		@Getter
		public static class Bot {

			@NotEmpty
			private String token;
		}
	}
}
