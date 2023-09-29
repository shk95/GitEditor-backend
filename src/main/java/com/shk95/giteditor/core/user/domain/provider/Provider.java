package com.shk95.giteditor.core.user.domain.provider;

import com.shk95.giteditor.common.model.AbstractBaseTimeEntity;
import com.shk95.giteditor.common.model.AbstractOAuth2UserInfo;
import com.shk95.giteditor.core.user.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "provider_user")
@Entity
public class Provider extends AbstractBaseTimeEntity {

	@EmbeddedId
	private ProviderId providerId;

	@Column(name = "prv_user_email", nullable = false)
	private String providerEmail;

	@Column(name = "prv_user_login_id")
	private String providerLoginId;

	@Column(name = "prv_user_name")
	private String providerUserName;

	@Column(name = "prv_access_token")
	private String accessToken;

	@Column(name = "prv_user_img_url")
	private String providerImgUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "user_prv_typ"),
		@JoinColumn(name = "user_login_id")})
	private User user;

	public static void update(Provider provider, AbstractOAuth2UserInfo retrievedUserInfo) {
		provider.accessToken = retrievedUserInfo.getAccessToken();
		provider.providerEmail = retrievedUserInfo.getEmail();
		provider.providerLoginId = retrievedUserInfo.getLoginId();
		provider.providerUserName = retrievedUserInfo.getName();
		provider.providerImgUrl = retrievedUserInfo.getImageUrl();
	}

	// Provider.java
	public void setUser(User user) {
		this.user = user;
	}
}
