package com.shk95.giteditor.domain.model.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);

	Optional<User> findByEmailAddress(String email);

	Boolean existsByUsername(String username);

	Boolean existsByEmailAddress(String email);
}
