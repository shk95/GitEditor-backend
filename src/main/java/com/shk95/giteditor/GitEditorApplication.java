package com.shk95.giteditor;

import com.shk95.giteditor.config.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@EnableConfigurationProperties({ApplicationProperties.class})
@EnableCaching
@SpringBootApplication
public class GitEditorApplication {

	static {
		System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
	}

	public static void main(String[] args) {
		SpringApplication.run(GitEditorApplication.class, args);
	}
}
