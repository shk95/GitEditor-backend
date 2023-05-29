package com.shk95.giteditor.domain.model.user;

import com.shk95.giteditor.domain.common.model.AbstractBaseTimeEntity;
import com.shk95.giteditor.domain.common.security.Role;
import com.shk95.giteditor.domain.model.provider.Provider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "service_user")
@Entity
public class User extends AbstractBaseTimeEntity {

	@EmbeddedId
	private UserId userId;

	@Column(name = "user_email", unique = true, length = 100)
	private String defaultEmail;// oAuth user 는 null 가능

	@Enumerated(EnumType.STRING)
	@Column(name = "user_role", nullable = false, length = 20)
	private Role role;

	@Column(name = "user_psw", length = 128)
	private String password;// oAuth 로 가입시 비밀번호 없음

	@Column(name = "user_name", length = 50)
	private String username;

	@Column(name = "user_prf_img_url", length = 512)
	private String profileImageUrl;

	@Column(name = "user_email_verified")
	private boolean isUserEmailVerified;//TODO: email 유효성 가입시 체크 기능

	@Column(name = "user_enabled")
	private boolean isUserEnabled;//TODO: user 활성화 여부 체크 기능

	@Column(name = "user_email_verification", unique = true, length = 100)
	private String emailVerificationCode;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL,
		orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Provider> providers = new ArrayList<>();

	public void updateUserName(String username) {
		this.username = username;
	}

	public User updatePassword(String newPassword) {
		this.password = newPassword;
		return this;
	}

	public void deleteEmailVerificationCode() {
		this.emailVerificationCode = null;
	}

	public void updateUserStateEnable() {
		this.isUserEmailVerified = true;
		this.isUserEnabled = true;
	}

	public User updateProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return isUserEmailVerified == user.isUserEmailVerified
			&& isUserEnabled == user.isUserEnabled
			&& Objects.equals(userId, user.userId)
			&& Objects.equals(defaultEmail, user.defaultEmail)
			&& role == user.role
			&& Objects.equals(password, user.password)
			&& Objects.equals(username, user.username)
			&& Objects.equals(profileImageUrl, user.profileImageUrl)
			&& Objects.equals(providers, user.providers);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId, defaultEmail, role
			, password, username, profileImageUrl
			, isUserEmailVerified, isUserEnabled, providers);
	}

	@Override
	public String toString() {
		return "User{" +
			"userId=" + userId.get() +
			", defaultEmail='" + defaultEmail + '\'' +
			", role=" + role +
			", password='" + password + '\'' +
			", username='" + username + '\'' +
			", profileImageUrl='" + profileImageUrl + '\'' +
			", isUserEmailVerified=" + isUserEmailVerified +
			", isUserEnabled=" + isUserEnabled +
			", providers=" + providers +
			'}';
	}
}
