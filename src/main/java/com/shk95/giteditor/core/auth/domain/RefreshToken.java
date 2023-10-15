package com.shk95.giteditor.core.auth.domain;

import com.shk95.giteditor.config.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@RedisHash(value = "refresh", timeToLive = Constants.Jwt.ExpireTime.REFRESH_TOKEN_EXPIRE_TIME_FOR_REDIS)
public class RefreshToken {

	@Id
	private String accessToken;

	@Indexed
	private String refreshToken;
}
