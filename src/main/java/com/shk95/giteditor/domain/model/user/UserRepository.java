package com.shk95.giteditor.domain.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, UserId> {

	Optional<User> findByDefaultEmail(String email);

	Optional<User> findByEmailVerificationCode(String code);

	boolean existsByDefaultEmail(String email);

	@Query("SELECT u FROM User u JOIN FETCH u.providers p" +
		" WHERE u.userId = :userId AND p.providerId.providerType = u.userId.providerType")
	Optional<User> findByUserIdWithProvider(@Param("userId") UserId userId);
}
