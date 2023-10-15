package com.shk95.giteditor.core.user.adapter.out;

import com.shk95.giteditor.common.constant.ProviderType;
import com.shk95.giteditor.core.user.application.port.out.FetchUserProjectionPort;
import com.shk95.giteditor.core.user.application.port.out.UserCrudRepositoryPort;
import com.shk95.giteditor.core.user.application.port.out.projection.OpenAIAccessToken;
import com.shk95.giteditor.core.user.domain.user.User;
import com.shk95.giteditor.core.user.domain.user.UserId;
import com.shk95.giteditor.core.user.infrastructure.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UserRepositoryAdapter implements UserCrudRepositoryPort, FetchUserProjectionPort {

	private final JpaUserRepository jpaUserRepository;

	@Override
	public User save(User user) {
		return jpaUserRepository.save(user);
	}

	@Override
	public User saveAndFlush(User user) {
		return jpaUserRepository.saveAndFlush(user);
	}

	@Override
	public Optional<User> findUserByLoginIdOrEmail(String userIdOrEmailAddress) {
		Predicate<User> isLocal = user -> user.getUserId().getProviderType() == ProviderType.LOCAL;
		return userIdOrEmailAddress.contains("@")
			? this.findUserByDefaultEmail(userIdOrEmailAddress).filter(isLocal)
			: jpaUserRepository.findById(new UserId(ProviderType.LOCAL, userIdOrEmailAddress));
	}

	@Override
	public Optional<User> findUserByUserId(UserId userId) {
		return jpaUserRepository.findById(userId);
	}

	@Override
	public Optional<User> findUserByDefaultEmail(String email) {
		return jpaUserRepository.findByDefaultEmail(email);
	}

	@Override
	public Optional<User> findByEmailVerificationCode(String code) {
		return jpaUserRepository.findByEmailVerificationCode(code);
	}

	@Override
	public Optional<User> findUserWithProviderById(UserId userId) {
		return jpaUserRepository.findUserWithProvidersByUserId(userId);
	}

	@Override
	public boolean existsById(UserId userId) {
		return jpaUserRepository.existsById(userId);
	}

	@Override
	public boolean existsByDefaultEmail(String email) {
		return jpaUserRepository.existsByDefaultEmail(email);
	}

	@Override
	public void deleteById(UserId userId) {
		jpaUserRepository.deleteById(userId);
	}

	@Override
	public Optional<OpenAIAccessToken> fetchOpenAIAccessToken(UserId userId) {
		return jpaUserRepository.findOpenAiAccessToken(userId);
	}
}
