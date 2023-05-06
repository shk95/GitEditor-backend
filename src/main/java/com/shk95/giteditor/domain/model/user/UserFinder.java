package com.shk95.giteditor.domain.model.user;

import com.shk95.giteditor.domain.model.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFinder {

	private final UserRepository userRepository;

	public User find(String usernameOrEmailAddress) throws UserNotFoundException {
		User user;
		if (usernameOrEmailAddress.contains("@")) {
			user = userRepository.findByEmailAddress(usernameOrEmailAddress).orElseThrow(UserNotFoundException::new);
		} else {
			user = userRepository.findByUsername(usernameOrEmailAddress).orElseThrow(UserNotFoundException::new);
		}
		return user;
	}
}
