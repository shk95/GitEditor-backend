package com.shk95.giteditor.domain.model.provider;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

import static com.shk95.giteditor.config.ConstantFields.REDIRECT_SIGNUP_OAUTH_EXPIRE;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "SignupOAuth", timeToLive = REDIRECT_SIGNUP_OAUTH_EXPIRE)
public class ProviderLoginInfo implements Serializable {
	private static final long serialVersionUID = 877823949039674411L;

	@Id
	private String id;
	private String providerType;
	private String loginId;
	private String email;
	private String name;
	private String imgUrl;
}
