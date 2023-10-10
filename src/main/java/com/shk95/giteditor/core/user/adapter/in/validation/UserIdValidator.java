package com.shk95.giteditor.core.user.adapter.in.validation;

import com.shk95.giteditor.common.constant.ProviderType;
import com.shk95.giteditor.core.user.application.port.out.UserCrudRepositoryPort;
import com.shk95.giteditor.core.user.application.service.command.SignupCommand;
import com.shk95.giteditor.core.user.domain.user.UserId;
import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@RequiredArgsConstructor
@Component
public class UserIdValidator implements Validator {

	private final UserCrudRepositoryPort userRepository;
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
