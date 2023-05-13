package com.shk95.giteditor.domain.model.user;

import com.shk95.giteditor.domain.common.model.BaseTimeEntity;
import com.shk95.giteditor.domain.common.security.UserDetailsImpl;
import com.shk95.giteditor.domain.common.security.oauth.ProviderType;
import com.shk95.giteditor.domain.model.roles.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "SERVICE_USER",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = "USER_ID"),
		@UniqueConstraint(columnNames = "USER_EMAIL")
	})
@Entity
public class User extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userSeq;

	@Column(name = "USER_ID", nullable = false, unique = true, length = 50)
	private String userId;

	@Column(name = "USER_EMAIL", nullable = false, unique = true, length = 100)
	private String defaultEmail;

	@Column(name = "USER_PSW", nullable = false, length = 128)
	private String password;

	@Column(name = "USER_ROLE", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	private Role role;

	@Column(name = "USER_NAME", length = 50)
	private String username;

	@Column(name = "USER_PRV_EMAIL", length = 100)
	private String providerEmail;

	@Column(name = "USER_PRV_TYP", length = 20)
	@Enumerated(EnumType.STRING)
	private ProviderType providerType;

	@Column(name = "USER_PRF_IMG_URL", length = 512)
	private String profileImageUrl;

	public static UserBuilder createUserBuilder(UserDetailsImpl userDetails) {
		return User.builder()
			.userId(userDetails.getUsername())
			.password(userDetails.getPassword())
			.defaultEmail(userDetails.getDefaultEmail())
			.username(userDetails.getUsername());
	}

	public void updateUserName(String username) {
		this.username = username;
	}

	public void updateProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	//TODO: equals hashcode, toString 오버라이딩 수정
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof User)) return false;
		User user = (User) o;
		return Objects.equals(userId, user.userId) &&
			Objects.equals(defaultEmail, user.defaultEmail);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId, defaultEmail);
	}

	@Override
	public String toString() {
		return "User{" +
			"id=" + userSeq +
			", username=`" + userId + "`" +
			", default email=`" + defaultEmail + "`" +
			", password=<Protected> " +
			"}";
	}
}
