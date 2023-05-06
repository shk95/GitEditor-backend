package com.shk95.giteditor.infrastructure.repository.redis;

import com.shk95.giteditor.config.ExpireTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "refresh", timeToLive = ExpireTime.REFRESH_TOKEN_EXPIRE_TIME_FOR_REDIS)
public class RefreshToken {

	@Id
	private String id;//username

	private String ip;

	private Collection<? extends GrantedAuthority> authorities;

	@Indexed
	private String refreshToken;
}
