package com.shk95.giteditor.domain.model.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * User registration domain service
 */
@Component
@Deprecated
public class RegistrationManagement {

	private UserRepository repository;
	private PasswordEncoder passwordEncryptor;

	public RegistrationManagement(UserRepository repository, PasswordEncoder passwordEncryptor) {
		this.repository = repository;
		this.passwordEncryptor = passwordEncryptor;
	}

	public User register(String username, String emailAddress, String password)
		throws RegistrationException {
		User existingUser = repository.findByUsername(username).get();
		if (existingUser != null) {
			throw new UsernameExistsException();
		}

		existingUser = repository.findByEmailAddress(emailAddress.toLowerCase());
		if (existingUser != null) {
			throw new EmailAddressExistsException();
		}

		String encryptedPassword = passwordEncryptor.encode(password);
		User newUser = User.builder().username(username).emailAddress(emailAddress.toLowerCase()).password(encryptedPassword).build();
		repository.save(newUser);
		return newUser;
	}
}
