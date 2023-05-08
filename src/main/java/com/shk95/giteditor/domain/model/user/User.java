package com.shk95.giteditor.domain.model.user;


import com.shk95.giteditor.domain.common.model.BaseTimeEntity;
import com.shk95.giteditor.domain.model.roles.Authority;
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
@Table(name = "users",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = "username"),
		@UniqueConstraint(columnNames = "email_address")
	})
@Entity
public class User extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "username", nullable = false, length = 50, unique = true)
	private String username;// user id

	@Column(name = "email_address", nullable = false, length = 100, unique = true)
	private String emailAddress;

	@Column(name = "password", nullable = false, length = 128)
	private String password;

	@Column
	@ElementCollection(fetch = FetchType.EAGER)
	@Builder.Default
	private List<Authority> roles = new ArrayList<>();

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof User)) return false;
		User user = (User) o;
		return Objects.equals(username, user.username) &&
			Objects.equals(emailAddress, user.emailAddress);
	}

	@Override
	public int hashCode() {
		return Objects.hash(username, emailAddress);
	}

	@Override
	public String toString() {
		return "User{" +
			"id=" + id +
			", username='" + username + '\'' +
			", emailAddress='" + emailAddress + '\'' +
			", password=<Protected> " +
			'}';
	}
}
