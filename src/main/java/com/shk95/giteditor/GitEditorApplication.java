package com.shk95.giteditor;

import com.shk95.giteditor.config.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
	ApplicationProperties.class
})
public class GitEditorApplication {

	public static void main(String[] args) {
		SpringApplication.run(GitEditorApplication.class, args);
	}
}
