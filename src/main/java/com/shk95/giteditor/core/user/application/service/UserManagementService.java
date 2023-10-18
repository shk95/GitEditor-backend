package com.shk95.giteditor.core.user.application.service;

import com.shk95.giteditor.common.constant.ProviderType;
import com.shk95.giteditor.common.service.file.FileStorage;
import com.shk95.giteditor.common.service.file.FileStorageResolver;
import com.shk95.giteditor.common.service.file.FileUrlCreator;
import com.shk95.giteditor.common.service.file.TempFile;
import com.shk95.giteditor.common.service.img.ThumbnailCreator;
import com.shk95.giteditor.common.service.mail.MailManager;
import com.shk95.giteditor.common.service.mail.MailTemplate;
import com.shk95.giteditor.common.service.mail.MessageVariable;
import com.shk95.giteditor.common.utils.ImageUtils;
import com.shk95.giteditor.common.utils.string.PasswordUtil;
import com.shk95.giteditor.core.auth.domain.CustomUserDetails;
import com.shk95.giteditor.core.openai.application.port.in.command.UpdateOpenAIServiceCommand;
import com.shk95.giteditor.core.openai.application.port.out.GptApiPort;
import com.shk95.giteditor.core.user.adapter.in.DiscordDto;
import com.shk95.giteditor.core.user.application.port.in.ManageUserUseCase;
import com.shk95.giteditor.core.user.application.port.out.GithubTokenHolderPort;
import com.shk95.giteditor.core.user.application.port.out.SendMailPort;
import com.shk95.giteditor.core.user.application.port.out.UserCrudRepositoryPort;
import com.shk95.giteditor.core.user.application.service.command.*;
import com.shk95.giteditor.core.user.domain.user.GithubService;
import com.shk95.giteditor.core.user.domain.user.User;
import com.shk95.giteditor.core.user.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserManagementService implements ManageUserUseCase {

	private final FileStorageResolver fileStorageResolver;
	private final FileUrlCreator fileUrlCreator;
	private final ThumbnailCreator thumbnailCreator;
	private final PasswordEncoder encoder;

	private final GptApiPort gptApiPort;
	private final SendMailPort sendMailPort;
	private final UserCrudRepositoryPort userCrudRepositoryPort;
	private final GithubTokenHolderPort githubTokenHolderPort;

	private final MailManager mailManager;

	@Transactional
	@Override
	public boolean verifyEmail(String emailVerificationCode) {// 변경된 이메일, 신규가입자 이메일 검증.
		return userCrudRepositoryPort.findByEmailVerificationCode(emailVerificationCode)
			.map(user -> {
				user.deleteEmailVerificationCode();
				user.activateEmailVerification();
				user.updateEmail();
				user.changeRoleFromTempToUser();// 최초회원가입 유저인경우.
				return true;
			}).orElse(false);
	}

	@Transactional
	@Override
	public void addGithubAccount(AddGithubAccountCommand command) {
		githubTokenHolderPort.save(new GithubService(command.getUserId().get()));
	}

	@Transactional
	@Override
	public void updateUsername(UpdateUserCommand command) {
		userCrudRepositoryPort.findUserByUserId(command.getUserId())
			.ifPresent(user -> user.updateUserName(command.getUsername()));
	}

	@Transactional
	@Override
	public void updatePassword(UpdateUserCommand command) {
		userCrudRepositoryPort.findUserByUserId(command.getUserId())
			.ifPresent(user -> user.updatePassword(encoder.encode(command.getPassword())));
	}

	@Transactional
	@Override
	public boolean updatePassword(UpdatePasswordCommand passwordCommand) {
		return passwordCommand.isPasswordForgot()
			? this.updatePassword(passwordCommand.getDefaultEmail())
			: this.updatePassword(passwordCommand.getUserId(), passwordCommand.getInputPassword());
	}

	private boolean updatePassword(UserId userId, String inputPassword) {
		return userCrudRepositoryPort.findUserByUserId(userId)
			.map(user -> {
				user.updatePassword(encoder.encode(inputPassword));
				return true;
			}).orElse(false);
	}

	private boolean updatePassword(String email) {
		Function<User, Boolean> updatePasswordAndSendMail = user -> {
			CustomUserDetails userDetails = CustomUserDetails.of(user);
			Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);

			String newPassword = PasswordUtil.generate(16);
			user.updatePassword(encoder.encode(newPassword));
			mailManager.send(
				user.getDefaultEmail(), "[GitEditor] 비밀번호 발급 안내", MailTemplate.NEW_PASSWORD
				, MessageVariable.from("password", newPassword));
			return true;
		};
		return userCrudRepositoryPort.findUserByDefaultEmail(email).map(updatePasswordAndSendMail).orElse(false);
	}

	@Transactional
	@Override
	public boolean updateEmail(UpdateEmailCommand command) {
		String newEmail = command.getEmail();
		return userCrudRepositoryPort.findUserByUserId(command.getUserId())
			.filter(user ->
				(user.getDefaultEmail() == null) || (!user.getDefaultEmail().equals(newEmail))
			)
			.map(user -> {
				user.addEmailToBeChanged(newEmail);
				user.deactivateEmailVerified();
				user.addEmailVerificationCode(sendMailPort.sendRegisterVerificationEmail(newEmail));
				if (user.getUserId().getProviderType() == ProviderType.LOCAL) {
					user.changeRoleFromUserToTemp();
				}
				return true;
			})
			.orElse(false);
	}

	@Transactional
	@Override
	public boolean uploadProfileImage(UserId userId, String folder, MultipartFile file) {
		FileStorage fileStorage = fileStorageResolver.resolve();
		TempFile tempImageFile = fileStorage.saveAsTempFile(folder, file);
		fileStorage.saveTempFile(tempImageFile);// Save the temp original image file to its target location
		thumbnailCreator.create(fileStorage, tempImageFile);// Create a thumbnail of the image file
		try {
			Files.delete(tempImageFile.getFile().toPath());
		} catch (IOException e) {
			log.error("Failed to delete temp file `" + tempImageFile.getFile().getAbsolutePath() + "`", e);
		}
		String uploadedImageUrl = fileUrlCreator.url(ImageUtils.getThumbnailVersion(tempImageFile.getFileRelativePath()));
		return userCrudRepositoryPort.findUserByUserId(userId)
			.map(u -> u.updateProfileImageUrl(uploadedImageUrl))
			.isPresent();
	}

	@Transactional
	@Override
	public boolean updateOpenAIService(UpdateOpenAIServiceCommand command) {
		if (!gptApiPort.isAvailable(command.accessToken())) {
			return false;
		}
		return userCrudRepositoryPort.findUserByUserId(command.userId())
			.map(user -> {
				user.upsertOpenAIToken(command.accessToken());
				user.activateOpenAIUsage();
				return true;
			}).orElse(false);
	}

	@Transactional
	@Override
	public boolean updateDiscordId(DiscordDto dto) {
		try {
			userCrudRepositoryPort.findUserByUserId(dto.getUserId())
				.ifPresent(user -> user.updateDiscordId(dto.getDiscordId()));
		} catch (Exception e) {
			log.info("failed to update discord id. {}", e.getMessage());
			return false;
		}
		return true;
	}

	@Transactional
	@Override
	public void deleteUser(DeleteUserCommand command) {
		userCrudRepositoryPort.deleteById(command.getUserId());
	}
}
