package com.shk95.giteditor.domain.model.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUserId(String username);

	Optional<User> findByDefaultEmail(String email);

	Optional<User> findByProviderEmail(String email);

	Boolean existsByUserId(String username);

	Boolean existsByDefaultEmail(String email);
}
