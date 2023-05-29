package com.shk95.giteditor.domain.model.user;

import com.shk95.giteditor.domain.common.constant.ProviderType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Predicate;

@RequiredArgsConstructor
@Component
public class UserFinder {

	private final UserRepository userRepository;

	public Optional<User> find(String userIdOrEmailAddress) {// 서비스에 직접 가입한 사용자만 사용
		Predicate<User> isLocal = user -> user.getUserId().getProviderType() == ProviderType.LOCAL;
		return userIdOrEmailAddress.contains("@")
			? userRepository.findByDefaultEmail(userIdOrEmailAddress).filter(isLocal)
			: userRepository.findById(new UserId(ProviderType.LOCAL, userIdOrEmailAddress));
	}

	public Optional<User> find(UserId userId) {
		return userId.getProviderType() == ProviderType.LOCAL
			? this.find(userId.getUserLoginId())
			: userRepository.findByUserIdWithProvider(userId);
	}
}
