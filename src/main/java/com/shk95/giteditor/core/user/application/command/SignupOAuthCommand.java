package com.shk95.giteditor.core.user.application.command;

import com.shk95.giteditor.common.constant.ProviderType;
import com.shk95.giteditor.core.user.domain.provider.ProviderLoginInfo;
import com.shk95.giteditor.core.user.adapter.AuthRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignupOAuthCommand {
	private String defaultUserId;
	private String defaultUsername;

	private ProviderType oAuthUserProviderType;
	private String oAuthUserId;
	private String oAuthUserLoginId;
	private String oAuthUserEmail;
	private String oAuthUserName;
	private String oAuthUserImgUrl;

	public SignupOAuthCommand set(AuthRequest.Signup.OAuth userInfo) {
		this.setDefaultUserId(userInfo.getUserId());
		this.setDefaultUsername(userInfo.getUsername());
		return this;
	}

	public SignupOAuthCommand set(ProviderLoginInfo userInfo) {
		this.setOAuthUserId(userInfo.getId());
		this.setOAuthUserLoginId(userInfo.getLoginId());
		this.setOAuthUserName(userInfo.getName());
		this.setOAuthUserEmail(userInfo.getEmail());
		this.setOAuthUserProviderType(ProviderType.valueOf(userInfo.getProviderType()));
		this.setOAuthUserImgUrl(userInfo.getImgUrl());
		return this;
	}
}
