package com.shk95.giteditor.domain.model.user;

import com.shk95.giteditor.domain.common.constant.ProviderType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUserIdAndProviderType(String userId, ProviderType providerType);

	Optional<User> findByDefaultEmail(String email);

	Boolean existsByUserIdAndProviderType(String userId, ProviderType providerType);

	Boolean existsByDefaultEmail(String email);
}
