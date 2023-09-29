package com.shk95.giteditor.core.user.adapter.validator;

import com.shk95.giteditor.core.user.application.command.SignupCommand;
import com.shk95.giteditor.common.constant.ProviderType;
import com.shk95.giteditor.core.user.domain.user.UserId;
import com.shk95.giteditor.core.user.domain.user.UserRepository;
import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@RequiredArgsConstructor
@Component
public class UserIdValidator implements Validator {
	private final UserRepository userRepository;
	private ProviderType providerType = null;

	public void setProviderType(ProviderType providerType) {
		this.providerType = providerType;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.equals(SignupCommand.Default.class) || clazz.equals(SignupCommand.OAuth.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		String userId = null;
		Assert.notNull(providerType, "Provider Type may not null");
		if (target instanceof SignupCommand.Default) {
			userId = ((SignupCommand.Default) target).getUserId();

		} else if (target instanceof SignupCommand.OAuth) {
			userId = ((SignupCommand.OAuth) target).getUserId();
		}
		if (userRepository.existsById(new UserId(this.providerType, userId))) {
			errors.rejectValue("userId", "Duplicate.userId");
		}
	}
}
