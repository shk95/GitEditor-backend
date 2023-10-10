package com.shk95.giteditor.core.user.domain.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;

import static com.shk95.giteditor.config.Constants.ADD_GITHUB_ACCOUNT_REDIS_EXPIRATION;

@Getter
@NoArgsConstructor
@RedisHash(value = "add_github_service", timeToLive = ADD_GITHUB_ACCOUNT_REDIS_EXPIRATION)
public class GithubService implements Serializable {

	@Serial
	private static final long serialVersionUID = 34282394902347411L;

	@Id
	private String userEntityId;

	public GithubService(String userEntityId) {
		this.userEntityId = userEntityId;
	}
}
