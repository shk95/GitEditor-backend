package com.shk95.giteditor.domain.model.user;

import com.shk95.giteditor.domain.common.constant.ProviderType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class UserFinder {

	private final UserRepository userRepository;

	public Optional<User> find(String userIdOrEmailAddress) {
		Optional<User> user;
		if (userIdOrEmailAddress.contains("@")) {
			user = userRepository.findByDefaultEmail(userIdOrEmailAddress);
		} else {
			user = userRepository.findByUserIdAndProviderType(userIdOrEmailAddress, ProviderType.LOCAL);
		}
		return user;
	}
}
