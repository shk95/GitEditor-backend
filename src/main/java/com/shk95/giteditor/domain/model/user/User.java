package com.shk95.giteditor.domain.model.user;

import com.shk95.giteditor.domain.common.model.BaseTimeEntity;
import com.shk95.giteditor.domain.common.security.CustomUserDetails;
import com.shk95.giteditor.domain.common.constant.ProviderType;
import com.shk95.giteditor.domain.model.provider.Provider;
import com.shk95.giteditor.domain.common.security.Role;
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
public class User extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_seq")
	private Long userSeq;

	@Column(name = "user_email", unique = true, length = 100)
	private String defaultEmail;// oAuth user 는 null 가능

	@Column(name = "user_id", nullable = false, length = 50)
	private String userId;// id 는 중복이 생길 가능성 있음. provider 끼리는 중복안됨.

	@Enumerated(EnumType.STRING)
	@Column(name = "user_prv_typ", nullable = false, length = 20)
	private ProviderType providerType;

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
