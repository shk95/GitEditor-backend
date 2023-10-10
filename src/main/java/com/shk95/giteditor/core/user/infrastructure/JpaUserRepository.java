package com.shk95.giteditor.core.user.infrastructure;

import com.shk95.giteditor.core.user.application.port.out.projection.OpenAIAccessToken;
import com.shk95.giteditor.core.user.domain.user.User;
import com.shk95.giteditor.core.user.domain.user.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<User, UserId> {

	Optional<User> findByDefaultEmail(String email);

	Optional<User> findByEmailVerificationCode(String code);

	@Query("SELECT u FROM User u LEFT JOIN FETCH u.providers WHERE u.userId = :userId")
	Optional<User> findUserWithProvidersByUserId(@Param("userId") UserId userId);

	@Query("SELECT u.openAIToken as openAIToken FROM User u WHERE u.userId= :userId")
	Optional<OpenAIAccessToken> findOpenAiAccessToken(@Param("userId") UserId userId);

	boolean existsByDefaultEmail(String email);
}