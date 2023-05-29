package com.shk95.giteditor.web.validator;

import com.shk95.giteditor.domain.common.constant.ProviderType;
import com.shk95.giteditor.domain.model.user.UserId;
import com.shk95.giteditor.domain.model.user.UserRepository;
import com.shk95.giteditor.web.apis.request.AuthRequest;
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
		return clazz.equals(AuthRequest.Signup.Default.class) || clazz.equals(AuthRequest.Signup.OAuth.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		String userId = null;
		if (target instanceof AuthRequest.Signup.Default) {
			userId = ((AuthRequest.Signup.Default) target).getUserId();

		} else if (target instanceof AuthRequest.Signup.OAuth) {
			userId = ((AuthRequest.Signup.OAuth) target).getUserId();
		}
		if (userRepository.existsById(new UserId(this.providerType, userId))) {
			errors.rejectValue("userId", "Duplicate.userId");
		}
	}
}
