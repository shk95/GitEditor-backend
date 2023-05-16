package com.shk95.giteditor.domain.model.user;

import com.shk95.giteditor.domain.common.model.BaseTimeEntity;
import com.shk95.giteditor.domain.common.security.CustomUserDetails;
import com.shk95.giteditor.domain.common.constants.ProviderType;
import com.shk95.giteditor.domain.model.provider.Provider;
import com.shk95.giteditor.domain.common.constants.Role;
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
@Table(name = "service_user",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = "user_id"),
		@UniqueConstraint(columnNames = "user_email")
	})
@Entity
public class User extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_seq")
	private Long userSeq;

	@Column(name = "user_id", nullable = false, unique = true, length = 50)
	private String userId;

	@Column(name = "user_email", nullable = false, unique = true, length = 100)
	private String defaultEmail;

	@Column(name = "user_role", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	private Role role;

	@Column(name = "user_psw", length = 128)
	private String password;// oAuth 로 가입시 비밀번호 없음

	@Column(name = "user_name", length = 50)
	private String username;

	@Column(name = "user_prv_typ", length = 20)
	@Enumerated(EnumType.STRING)
	private ProviderType providerType;// oAuth 로 가입한경우

	@Column(name = "user_prf_img_url", length = 512)
	private String profileImageUrl;

	@Column(name = "user_email_verified")
	private boolean isUserEmailVerified;//TODO: email 유효성 가입시 체크 기능

	@Column(name = "user_disabled")
	private boolean isUserDisabled;//TODO: user 활성화 여부 체크 기능

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL,
		orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Provider> providers = new ArrayList<>();

	public static UserBuilder createUserBuilder(CustomUserDetails userDetails) {
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
