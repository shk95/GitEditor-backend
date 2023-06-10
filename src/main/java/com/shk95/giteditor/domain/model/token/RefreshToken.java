package com.shk95.giteditor.domain.model.token;

import com.shk95.giteditor.config.ConstantFields;
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
@RedisHash(value = "refresh", timeToLive = ConstantFields.Jwt.ExpireTime.REFRESH_TOKEN_EXPIRE_TIME_FOR_REDIS)
public class RefreshToken {

	@Id
	private String subject;// jwt subject

	@Indexed
	private String refreshToken;

	private String authorities;// 값 변환 필요. String <--> Collection<? extends GrantedAuthority>
}
