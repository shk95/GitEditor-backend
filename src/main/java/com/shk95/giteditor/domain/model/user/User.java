package com.shk95.giteditor.domain.model.user;


import com.shk95.giteditor.domain.common.model.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = "username"),
		@UniqueConstraint(columnNames = "email_address")
	})
public class User extends BaseTimeEntity implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "username", nullable = false, length = 50, unique = true)
	private String username;

	@Column(name = "email_address", nullable = false, length = 100, unique = true)
	private String emailAddress;

	@Column(name = "password", nullable = false, length = 128)
	private String password;

	@Column
	@ElementCollection(fetch = FetchType.EAGER)
	@Builder.Default
	private List<String> roles = new ArrayList<>();

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.roles.stream()
			.map(SimpleGrantedAuthority::new)
			.collect(Collectors.toList());
	}


	public String getEmailAddress() {
		return emailAddress;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public List<String> getRoles() {
		return roles;
	}

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
