package com.shk95.giteditor.domain.model.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, UserId> {

	Optional<User> findByDefaultEmail(String email);

	boolean existsByDefaultEmail(String email);
}
