package com.shk95.giteditor.domain.model.user;

import com.shk95.giteditor.domain.common.constant.ProviderType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class UserFinder {

	private final UserRepository userRepository;

	public Optional<User> find(String userIdOrEmailAddress) {// 서비스에 직접 가입한 사용자만 사용
		Optional<User> user;
		if (userIdOrEmailAddress.contains("@")) {
			user = userRepository.findByDefaultEmail(userIdOrEmailAddress);
		} else {
			user = userRepository.findById(new UserId(ProviderType.LOCAL, userIdOrEmailAddress));
		}
		return user;
	}

	public Optional<User> find(UserId userId) {
		return userRepository.findById(userId)
			.map(user -> {
					user.findProvider(user.getProviders().stream()
						.filter(provider
							-> provider.getProviderId().getProviderType().equals(user.getUserId().getProviderType()))
						.collect(Collectors.toList()).subList(0, 1));// 1개만 허용
					return user;
				}
			);
	}
}
