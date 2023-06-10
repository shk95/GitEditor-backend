package com.shk95.giteditor.config;

import com.shk95.giteditor.domain.model.user.UserId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

	@Bean
	public AuditorAware<UserId> auditorProvider() {
		return new SpringSecurityAuditorAware();
	}
}
