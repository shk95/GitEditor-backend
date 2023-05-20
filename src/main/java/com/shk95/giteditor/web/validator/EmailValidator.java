package com.shk95.giteditor.web.validator;

import com.shk95.giteditor.domain.model.user.UserRepository;
import com.shk95.giteditor.web.apis.request.AuthRequest;
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
		return clazz.equals(AuthRequest.Signup.Default.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		String email = ((AuthRequest.Signup.Default) target).getDefaultEmail();
		if (repository.existsByDefaultEmail(email)) {
			errors.rejectValue("defaultEmail", "Duplicate.email");
		}
	}
}
