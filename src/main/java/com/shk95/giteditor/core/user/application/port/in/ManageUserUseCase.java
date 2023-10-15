package com.shk95.giteditor.core.user.application.port.in;

import com.shk95.giteditor.core.openai.application.port.in.command.UpdateOpenAIServiceCommand;
import com.shk95.giteditor.core.user.application.service.command.*;
import com.shk95.giteditor.core.user.domain.user.UserId;
import org.springframework.web.multipart.MultipartFile;

public interface ManageUserUseCase {

	boolean verifyEmail(String emailVerificationCode);

	void addGithubAccount(AddGithubAccountCommand command);

	void updateUsername(UpdateUserCommand command);

	void updatePassword(UpdateUserCommand command);

	boolean updatePassword(UpdatePasswordCommand passwordCommand);

	boolean updateEmail(UpdateEmailCommand command);

	boolean uploadProfileImage(UserId userId, String folder, MultipartFile file);

	boolean updateOpenAIService(UpdateOpenAIServiceCommand command);

	void deleteUser(DeleteUserCommand command);
}
