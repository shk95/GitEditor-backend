package com.shk95.giteditor.domain.model.user;

import com.shk95.giteditor.domain.application.commands.UpdatePasswordCommand;
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
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserManagement {

	private final UserRepository userRepository;
	private final MailManager mailManager;
	private final FileStorageResolver fileStorageResolver;
	private final FileUrlCreator fileUrlCreator;
	private final ThumbnailCreator thumbnailCreator;
	private final PasswordEncoder encoder;

	private boolean updatePassword(String email) {
		Function<User, Boolean> thenUpdatePasswordAndSendMail = user -> {
			String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$";
			Xeger generator = new Xeger(regex);
			final String NEW_PASSWORD = generator.generate();
			user.updatePassword(encoder.encode(NEW_PASSWORD));
			mailManager.send(
				user.getDefaultEmail(), "[GitEditor] 비밀번호 변경", "findPassword"
				, MessageVariable.from("code", NEW_PASSWORD));
			return true;
		};
		return userRepository.findByDefaultEmail(email).map(thenUpdatePasswordAndSendMail).orElse(false);
	}

	@Transactional
	public boolean updatePassword(UpdatePasswordCommand passwordCommand) {
		return passwordCommand.isPasswordForgot()
			? this.updatePassword(passwordCommand.getDefaultEmail())
			: this.updatePassword(passwordCommand.getUserId(), passwordCommand.getInputPassword());
	}

	private boolean updatePassword(UserId userId, String inputPassword) {
		return userRepository.findById(userId)
			.map(user -> {
				user.updatePassword(encoder.encode(inputPassword));
				return true;
			}).orElse(false);
	}

	@Transactional
	public boolean verifyEmail(String emailVerificationCode) {// 회원가입 메일 인증
		return userRepository.findByEmailVerificationCode(emailVerificationCode)
			.map(user -> {
				user.deleteEmailVerificationCode();
				user.updateUserStateEnable();
				return true;
			}).orElse(false);
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
}
