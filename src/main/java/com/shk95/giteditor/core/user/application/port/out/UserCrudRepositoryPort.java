package com.shk95.giteditor.core.user.application.port.out;

import com.shk95.giteditor.core.user.domain.user.User;
import com.shk95.giteditor.core.user.domain.user.UserId;

import java.util.Optional;

public interface UserCrudRepositoryPort {

	User save(User user);

	User saveAndFlush(User user);

	Optional<User> findUserByLoginIdOrEmail(String userIdOrEmailAddress);// 서비스에 직접 가입한 사용자만 사용

	Optional<User> findUserByUserId(UserId userId);

	Optional<User> findUserByDefaultEmail(String email);

	Optional<User> findByEmailVerificationCode(String code);

	Optional<User> findUserWithProviderById(UserId userId);

	boolean existsById(UserId userId);

	boolean existsByDefaultEmail(String email);

	void deleteById(UserId userId);
}
