package com.shk95.giteditor.core.user.application.service;

import com.shk95.giteditor.common.constant.ProviderType;
import com.shk95.giteditor.common.security.Role;
import com.shk95.giteditor.core.user.adapter.in.SignupResult;
import com.shk95.giteditor.core.user.application.port.in.SignupUseCase;
import com.shk95.giteditor.core.user.application.port.out.ProviderRepositoryPort;
import com.shk95.giteditor.core.user.application.port.out.SendMailPort;
import com.shk95.giteditor.core.user.application.port.out.UserCrudRepositoryPort;
import com.shk95.giteditor.core.user.application.service.command.SignupCommand;
import com.shk95.giteditor.core.user.application.service.command.SignupOAuthCommand;
import com.shk95.giteditor.core.user.domain.provider.Provider;
import com.shk95.giteditor.core.user.domain.provider.ProviderId;
import com.shk95.giteditor.core.user.domain.user.User;
import com.shk95.giteditor.core.user.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class SignupService implements SignupUseCase {


	private final UserCrudRepositoryPort userCrudRepositoryPort;
	private final ProviderRepositoryPort providerRepositoryPort;
	private final PasswordEncoder passwordEncoder;
	private final SendMailPort sendMailPort;

	@Override
	@Transactional
	public SignupResult signupDefault(SignupCommand.Default signUp) {
		if (userCrudRepositoryPort.existsByDefaultEmail(signUp.getDefaultEmail())) {
			return SignupResult.fail().message("이미 회원가입된 이메일 입니다.").build();
		}
		if (userCrudRepositoryPort.existsById(new UserId(ProviderType.LOCAL, signUp.getUserId()))) {
			return SignupResult.fail().message("이미 회원가입된 아이디 입니다.").build();
		}
		String emailCode = sendMailPort.sendVerificationEmail(signUp.getDefaultEmail());
		User user = userCrudRepositoryPort.save(User.builder()
			.userId(new UserId(ProviderType.LOCAL, signUp.getUserId()))
			.password(passwordEncoder.encode(signUp.getPassword()))
			.defaultEmail(signUp.getDefaultEmail())
			.role(Role.TEMP)
			.username(signUp.getUsername())
			.userEmailVerified(false)
			.userEnabled(true)
			.emailVerificationCode(emailCode)
			.build());
		return SignupResult.success().message("회원가입에 성공했습니다.").build();
	}

	@Override
	@Transactional
	public Provider signupOAuthUser(SignupOAuthCommand command) {
		User user = User.builder().
			userId(new UserId(command.getOAuthUserProviderType(), command.getOAuthUserId()))
			.username(command.getDefaultUsername())
			.userEmailVerified(false)
			.userEnabled(true)
			.role(Role.USER)
			.profileImageUrl(command.getOAuthUserImgUrl())
			.githubEnabled(command.getOAuthUserProviderType() == ProviderType.GITHUB)
			.openAIEnabled(false)
			.build();
		User savedUser = userCrudRepositoryPort.saveAndFlush(user);
		ProviderId providerId = new ProviderId(command.getOAuthUserProviderType(), command.getOAuthUserId());
		Provider providerUser = Provider.builder()
			.providerId(providerId)
			.providerUserName(command.getOAuthUserName())
			.providerEmail(command.getOAuthUserEmail())
			.providerLoginId(command.getOAuthUserLoginId())
			.providerImgUrl(command.getOAuthUserImgUrl())
			.user(savedUser)
			.build();
		return providerRepositoryPort.save(providerUser);
	}
}
