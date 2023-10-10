package com.shk95.giteditor.core.user.adapter.in.validation;

import com.shk95.giteditor.core.user.application.port.out.UserCrudRepositoryPort;
import com.shk95.giteditor.core.user.application.service.command.SignupCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@RequiredArgsConstructor
@Component
public class EmailValidator implements Validator {

	private final UserCrudRepositoryPort repository;

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
