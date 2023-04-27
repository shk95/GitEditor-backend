package com.shk95.giteditor.domain.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);

	User findByEmailAddress(String email);

	Boolean existsByUsername(String username);

	Boolean existsByEmailAddress(String email);
}
