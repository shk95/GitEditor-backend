package com.shk95.giteditor.core.user.infrastructure;

import com.shk95.giteditor.core.user.application.port.out.projection.DiscordIdProjection;
import com.shk95.giteditor.core.user.application.port.out.projection.OpenAIAccessTokenProjection;
import com.shk95.giteditor.core.user.application.port.out.projection.SimpleUserProjection;
import com.shk95.giteditor.core.user.application.port.out.projection.UserIdProjection;
import com.shk95.giteditor.core.user.domain.user.User;
import com.shk95.giteditor.core.user.domain.user.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<User, UserId> {

	@Query("""
		SELECT u.discordId AS discordId
		FROM User u
		WHERE u.userId = :userId
		""")
	Optional<DiscordIdProjection> findDiscordIdByUserId(@Param("userId") UserId userId);

	@Query("""
		SELECT u.userId.userLoginId AS userLoginId,
		u.userId.providerType AS providerType
		FROM User u
		WHERE u.discordId = :discordId
		""")
	Optional<UserIdProjection> findUserIdByDiscordId(@Param("discordId") String discordId);

	@Query("""
		SELECT u.openAIToken as openAIToken
		FROM User u
		WHERE u.userId= :userId
		""")
	Optional<OpenAIAccessTokenProjection> findOpenAiAccessToken(@Param("userId") UserId userId);

	@Query("""
		SELECT u.userId.userLoginId AS userLoginId,
		u.userId.providerType AS providerType,
		u.username AS username,
		u.defaultEmail AS defaultEmail,
		u.profileImageUrl AS profileImageUrl
		FROM User u
		WHERE u.username LIKE :username
		""")
	List<SimpleUserProjection> findUserIdLikeUsername(@Param("username") String username);

	Optional<User> findByDefaultEmail(String email);

	Optional<User> findByEmailVerificationCode(String code);

	@Query("""
		SELECT u
		FROM User u
		LEFT JOIN FETCH u.providers
		WHERE u.userId = :userId
		""")
	Optional<User> findUserWithProvidersByUserId(@Param("userId") UserId userId);

	boolean existsByDefaultEmail(String email);

}
