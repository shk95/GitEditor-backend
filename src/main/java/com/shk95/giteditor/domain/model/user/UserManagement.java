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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.security.SecureRandom;
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

	public static String generatePassword() {
		final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		final String NUMBERS = "0123456789";
		final String SPECIAL_CHARACTERS = "~!@#$%^&*()+|=";
		final String ALL_CHARACTERS = ALPHABET + NUMBERS + SPECIAL_CHARACTERS;
		final int PASSWORD_LENGTH = 16;
		SecureRandom random = new SecureRandom();

		StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

		// Ensure at least one alphabet character, one number, and one special character
		password.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
		password.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
		password.append(SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length())));

		// Fill the rest with random characters
		for (int i = 3; i < PASSWORD_LENGTH; i++) {
			password.append(ALL_CHARACTERS.charAt(random.nextInt(ALL_CHARACTERS.length())));
		}

		// Shuffle the characters to ensure randomness
		for (int i = 0; i < password.length(); i++) {
			int j = random.nextInt(password.length());
			char temp = password.charAt(i);
			password.setCharAt(i, password.charAt(j));
			password.setCharAt(j, temp);
		}

		return password.toString();
	}

	@Transactional
	public boolean updatePassword(UpdatePasswordCommand passwordCommand) {
		return passwordCommand.isPasswordForgot()
			? this.updatePassword(passwordCommand.getDefaultEmail())
			: this.updatePassword(passwordCommand.getUserId(), passwordCommand.getInputPassword());
	}

	private boolean updatePassword(String email) {
		final String C_1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		final String C_2 = "abcdefghijklmnopqrstuvwxyz";
		final String C_3 = "0123456789";
		final String C_4 = "!@#$%^&*()+";
		SecureRandom random = new SecureRandom();

		Function<User, Boolean> updatePasswordAndSendMail = user -> {
			CustomUserDetails userDetails = CustomUserDetails.of(user);
			Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);

			StringBuilder password = new StringBuilder(16);
			for (int i = 0; i < 4; i++) {
				password.append(C_1.charAt(random.nextInt(C_1.length())));
				password.append(C_2.charAt(random.nextInt(C_3.length())));
				password.append(C_3.charAt(random.nextInt(C_3.length())));
				password.append(C_4.charAt(random.nextInt(C_4.length())));
			}
			final String NEW_PASSWORD = password.toString();
			user.updatePassword(encoder.encode(NEW_PASSWORD));
			mailManager.send(
				user.getDefaultEmail(), "[GitEditor] 비밀번호 발급 안내", "new-password.ftl"
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
				user.activateEmailVerified();
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
				user.deactivateEmailVerified();
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
				if (command.getUsername() != null) user.updateUserName(command.getUsername());
				if (command.getPassword() != null) user.updatePassword(encoder.encode(command.getPassword()));
				if (command.getEmail() != null) {
					sendVerificationEmail(command.getEmail());
					user.updateEmail(command.getEmail());
					user.deactivateEmailVerified();
					user.changeRoleFromUserToTemp();
				}
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
		githubServiceRepository.save(new GithubService(command.getUserId().get()));
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
