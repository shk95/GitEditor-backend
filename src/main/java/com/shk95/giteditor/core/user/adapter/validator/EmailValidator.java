package com.shk95.giteditor.core.user.adapter.validator;

import com.shk95.giteditor.core.user.application.command.SignupCommand;
import com.shk95.giteditor.core.user.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@RequiredArgsConstructor
@Component
public class EmailValidator implements Validator {
	private final UserRepository repository;

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.equals(SignupCommand.Default.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		String email = ((SignupCommand.Default) target).getDefaultEmail();
		if (repository.existsByDefaultEmail(email)) {
			errors.rejectValue("defaultEmail", "Duplicate.email");
		}
	}
}
