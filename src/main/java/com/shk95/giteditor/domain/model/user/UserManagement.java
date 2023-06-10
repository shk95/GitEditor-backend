package com.shk95.giteditor.domain.model.user;

import com.shk95.giteditor.config.ApplicationProperties;
import com.shk95.giteditor.domain.application.commands.*;
import com.shk95.giteditor.domain.common.constant.ProviderType;
import com.shk95.giteditor.domain.common.file.FileStorage;
import com.shk95.giteditor.domain.common.file.FileStorageResolver;
import com.shk95.giteditor.domain.common.file.FileUrlCreator;
import com.shk95.giteditor.domain.common.file.TempFile;
import com.shk95.giteditor.domain.common.img.ThumbnailCreator;
import com.shk95.giteditor.domain.common.mail.MailManager;
import com.shk95.giteditor.domain.common.mail.MessageVariable;
import com.shk95.giteditor.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.flotsam.xeger.Xeger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.UUID;
import java.util.function.Function;

import static com.shk95.giteditor.domain.model.chat.SimpleGpt.simpleResponse;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserManagement {

	private final UserRepository userRepository;
	private final GithubServiceRepository githubServiceRepository;
	private final MailManager mailManager;
	private final FileStorageResolver fileStorageResolver;
	private final FileUrlCreator fileUrlCreator;
	private final ThumbnailCreator thumbnailCreator;
	private final PasswordEncoder encoder;
	private final ApplicationProperties properties;

	@Transactional
	public boolean updatePassword(UpdatePasswordCommand passwordCommand) {
		return passwordCommand.isPasswordForgot()
			? this.updatePassword(passwordCommand.getDefaultEmail())
			: this.updatePassword(passwordCommand.getUserId(), passwordCommand.getInputPassword());
	}

	private boolean updatePassword(String email) {
		Function<User, Boolean> updatePasswordAndSendMail = user -> {
			String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$";
			Xeger generator = new Xeger(regex);
			final String NEW_PASSWORD = generator.generate();
			user.updatePassword(encoder.encode(NEW_PASSWORD));
			mailManager.send(
				user.getDefaultEmail(), "[GitEditor] 비밀번호 발급 안내", "new-password"
				, MessageVariable.from("password", NEW_PASSWORD));
			return true;
		};
		return userRepository.findByDefaultEmail(email).map(updatePasswordAndSendMail).orElse(false);
	}

	private boolean updatePassword(UserId userId, String inputPassword) {
		return userRepository.findById(userId)
			.map(user -> {
				user.updatePassword(encoder.encode(inputPassword));
				return true;
			}).orElse(false);
	}

	@Transactional
	public boolean verifyEmail(String emailVerificationCode) {// 변경된 이메일, 신규가입자 이메일 검증.
		return userRepository.findByEmailVerificationCode(emailVerificationCode)
			.map(user -> {
				user.deleteEmailVerificationCode();
				user.changeEmailVerified(true);
				user.updateEmailFromOld();
				user.changeRoleFromTempToUser();// 최초회원가입 유저인경우.
				return true;
			}).orElse(false);
	}

	@Transactional
	public boolean changeEmail(ChangeEmailCommand command) {
		String newEmail = command.getEmail();
		return userRepository.findById(command.getUserId())
			.filter(user ->
				!user.getDefaultEmail().equals(newEmail)
			)
			.map(user -> {
				user.addEmailToBeChanged(newEmail);
				user.changeEmailVerified(false);
				user.addEmailVerificationCode(this.sendVerificationEmail(newEmail));
				if (user.getUserId().getProviderType() == ProviderType.LOCAL) {
					user.changeRoleFromUserToTemp();
				}
				return true;
			})
			.orElse(false);
	}

	@Transactional
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
		return userRepository.findById(userId)
			.map(u -> u.updateProfileImageUrl(uploadedImageUrl))
			.isPresent();
	}

	public void deleteUser(DeleteUserCommand command) {
		userRepository.deleteById(command.getUserId());
	}

	@Transactional
	public boolean updateUser(UpdateUserCommand command) {
		return userRepository.findById(command.getUserId())
			.map(user -> {
				if (command.getPassword() != null) user.updateUserName(command.getUsername());
				if (command.getUsername() != null) user.updatePassword(command.getPassword());
				if (command.getEmail() != null) user.updateEmail(command.getEmail());
				return true;
			}).orElse(false);
	}

	@Transactional
	public boolean updateOpenAIService(UpdateOpenAIServiceCommand command) {
		if (!verifyOpenAI(command.getAccessToken())) {
			return false;
		}
		return userRepository.findById(command.getUserId())
			.map(user -> {
				user.upsertOpenAIToken(command.getAccessToken());
				user.activateOpenAIUsage();
				return true;
			}).orElse(false);
	}

	public void addGithubAccount(AddGithubAccountCommand command) {
		githubServiceRepository.save(new GithubService(command.getUserId()));
	}

	private String sendVerificationEmail(String email) {
		String code = Base64.getUrlEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
		String home = properties.getFrontPageUrl();
		mailManager.send(
			email,
			"이메일 변경 안내",
			"welcome.ftl",
			MessageVariable.from("code", home + "/redirect?type=emailVerification&code=" + code),
			MessageVariable.from("message", "아래의 링크를 클릭해서 이메일을 확인해 주세요.")
		);
		return code;
	}


	private boolean verifyOpenAI(String accessToken) {
		try {
			simpleResponse(accessToken, "just say hi");
		} catch (Exception e) {
			log.info("Failed to verify OpenAI access token");
			return false;
		}
		return true;
	}
}
