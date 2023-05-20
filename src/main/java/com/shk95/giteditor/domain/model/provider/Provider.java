package com.shk95.giteditor.domain.model.provider;

import com.shk95.giteditor.domain.common.model.BaseTimeEntity;
import com.shk95.giteditor.domain.common.security.info.OAuth2UserInfo;
import com.shk95.giteditor.domain.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "provider_user")
@Entity
public class Provider extends BaseTimeEntity {

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
	@JoinColumn(name = "user_seq")
	private User user;

	public static void update(Provider provider, OAuth2UserInfo retrievedUserInfo) {
		provider.accessToken = retrievedUserInfo.getAccessToken();
		provider.providerEmail = retrievedUserInfo.getEmail();
		provider.providerLoginId = retrievedUserInfo.getLoginId();
		provider.providerUserName = retrievedUserInfo.getName();
		provider.providerImgUrl = retrievedUserInfo.getImageUrl();
	}
}
