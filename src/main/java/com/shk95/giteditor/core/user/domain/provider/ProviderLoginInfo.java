package com.shk95.giteditor.core.user.domain.provider;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;

import static com.shk95.giteditor.config.Constants.REDIRECT_SIGNUP_OAUTH_EXPIRE;

@Getter
@NoArgsConstructor
@RedisHash(value = "SignupOAuth", timeToLive = REDIRECT_SIGNUP_OAUTH_EXPIRE)
public class ProviderLoginInfo implements Serializable {

	@Serial
	private static final long serialVersionUID = 877823949039674411L;

	@Id
	private String id;
	private String providerType;
	private String loginId;
	private String email;
	private String name;
	private String imgUrl;

	@Builder
	public ProviderLoginInfo(String id, String providerType, String loginId, String email, String name, String imgUrl) {
		this.id = id;
		this.providerType = providerType;
		this.loginId = loginId;
		this.email = email;
		this.name = name;
		this.imgUrl = imgUrl;
	}
}
