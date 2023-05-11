package com.shk95.giteditor.domain.model.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserFinder {

	private final UserRepository userRepository;

	public Optional<User> find(String usernameOrEmailAddress) {
		Optional<User> user;
		if (usernameOrEmailAddress.contains("@")) {
			user = userRepository.findByDefaultEmail(usernameOrEmailAddress);
		} else {
			user = userRepository.findByUserId(usernameOrEmailAddress);
		}
		return user;
	}
}
