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
	private boolean isUserEmailVerified;

	@Column(name = "user_enabled")
	private boolean isUserEnabled;//TODO: user 활성화 여부 체크 기능

	@Column(name = "user_email_verification", unique = true, length = 100)
	private String emailVerificationCode;

	@Column(name = "user_email_new", length = 50)
	private String emailToBeChanged;

	@Column(name = "user_openai_token", length = 128)
	private String openAIToken;

	@Column(name = "user_github_enabled")
	private boolean isGithubEnabled;

	@Column(name = "user_openai_enabled")
	private boolean isOpenAIEnabled;

	@Builder.Default
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL,
		orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Provider> providers = new ArrayList<>();

	public void addProvider(Provider provider) {
		this.providers.add(provider);
		provider.setUser(this);
	}

	public void updateUserName(String username) {
		this.username = username;
	}

	public void updatePassword(String newPassword) {
		this.password = newPassword;
	}

	public User activateGithubUsage() {
		this.isGithubEnabled = true;
		return this;
	}

	public User deactivateGithubUsage() {
		this.isGithubEnabled = false;
		return this;
	}

	public void deleteEmailVerificationCode() {
		this.emailVerificationCode = null;
	}

	public void addEmailVerificationCode(String emailVerificationCode) {
		this.emailVerificationCode = emailVerificationCode;
	}

	public void activateOpenAIUsage() {
		this.isOpenAIEnabled = true;
	}

	public void deactivateOpenAIUsage() {
		this.isOpenAIEnabled = false;
	}

	public void activateEmailVerified() {
		this.isUserEmailVerified = true;
	}

	public void deactivateEmailVerified() {
		this.isUserEmailVerified = false;
	}

	public void updateEmail(String email) {
		this.defaultEmail = email;
	}

	public User updateProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
		return this;
	}

	public void upsertOpenAIToken(String accessToken) {
		this.openAIToken = accessToken;
	}

	public void activateUser() {
		this.isUserEnabled = true;
	}

	public void deActivateUser() {
		this.isUserEnabled = false;
	}

	public void changeRoleFromTempToUser() {
		if (this.role == Role.TEMP) {
			this.role = Role.USER;
		}
	}

	public void changeRoleFromUserToTemp() {
		if (this.role == Role.USER) {
			this.role = Role.TEMP;
		}
	}

	public void addEmailToBeChanged(String emailToBeChanged) {
		this.emailToBeChanged = emailToBeChanged;
	}

	public void updateEmailFromOld() {
		if (this.emailToBeChanged != null) {
			this.defaultEmail = this.emailToBeChanged;
			this.emailToBeChanged = null;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return isUserEmailVerified == user.isUserEmailVerified && isUserEnabled == user.isUserEnabled && Objects.equals(userId, user.userId) && Objects.equals(defaultEmail, user.defaultEmail) && role == user.role && Objects.equals(password, user.password) && Objects.equals(username, user.username) && Objects.equals(profileImageUrl, user.profileImageUrl) && Objects.equals(emailVerificationCode, user.emailVerificationCode) && Objects.equals(emailToBeChanged, user.emailToBeChanged) && Objects.equals(providers, user.providers);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId, defaultEmail, role, password, username, profileImageUrl, isUserEmailVerified, emailVerificationCode, emailToBeChanged, isUserEnabled, providers);
	}

	@Override
	public String toString() {
		return "User{" +
			"userId=" + userId +
			", defaultEmail='" + defaultEmail + '\'' +
			", role=" + role +
			", password='" + "[password]" + '\'' +
			", username='" + username + '\'' +
			", profileImageUrl='" + profileImageUrl + '\'' +
			", isUserEmailVerified=" + isUserEmailVerified +
			", emailVerificationCode='" + emailVerificationCode + '\'' +
			", emailToBeChanged='" + emailToBeChanged + '\'' +
			", isUserEnabled=" + isUserEnabled +
			", providers=" + providers +
			'}';
	}
}
