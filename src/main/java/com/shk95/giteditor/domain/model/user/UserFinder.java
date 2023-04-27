package com.shk95.giteditor.domain.model.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Deprecated
@Component
@RequiredArgsConstructor
public class UserFinder {

	private final UserRepository userRepository;

	public User find(String usernameOrEmailAddress) throws UserNotFoundException {
		User user;
		if (usernameOrEmailAddress.contains("@")) {
			user = userRepository.findByEmailAddress(usernameOrEmailAddress);
		} else {
			user = userRepository.findByUsername(usernameOrEmailAddress).get();
		}
		if (user == null) {
			throw new UserNotFoundException();
		}
		return user;
	}
}
