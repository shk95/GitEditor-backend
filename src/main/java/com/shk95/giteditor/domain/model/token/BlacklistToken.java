package com.shk95.giteditor.domain.model.token;

import com.shk95.giteditor.config.ConstantFields;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
@RedisHash(value = "logout", timeToLive = ConstantFields.Jwt.ExpireTime.ACCESS_TOKEN_EXPIRE_TIME)
public class BlacklistToken {

	@Id
	private String accessToken;

	@TimeToLive(unit = TimeUnit.MILLISECONDS)
	private Long expiration;
}
