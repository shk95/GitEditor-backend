package com.shk95.giteditor.core.user.adapter.in;

import com.shk95.giteditor.common.security.CurrentUser;
import com.shk95.giteditor.common.security.UserAuthorize;
import com.shk95.giteditor.common.security.UserOrTempAuthorize;
import com.shk95.giteditor.common.utils.ImageUtils;
import com.shk95.giteditor.common.utils.Response;
import com.shk95.giteditor.common.utils.web.CookieUtil;
import com.shk95.giteditor.core.auth.domain.CustomUserDetails;
import com.shk95.giteditor.core.openai.application.port.in.command.UpdateOpenAIServiceCommand;
import com.shk95.giteditor.core.user.application.port.in.FetchUserInfoUseCase;
import com.shk95.giteditor.core.user.application.port.in.ManageUserUseCase;
import com.shk95.giteditor.core.user.application.service.command.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.shk95.giteditor.config.Constants.ADD_GITHUB_ACCOUNT_REDIS_EXPIRATION;
import static com.shk95.giteditor.config.Constants.ADD_OAUTH_SERVICE_USER_INFO;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserManagementController {

	private final ManageUserUseCase manageUserUseCase;
	private final FetchUserInfoUseCase fetchUserInfoUseCase;

	@UserOrTempAuthorize
	@GetMapping("/profile")
	public ResponseEntity<?> me(@CurrentUser CustomUserDetails userDetails) {
		UserResponse.Me me = fetchUserInfoUseCase.fetchUser(userDetails.getUserId())
			.map(UserResponse.Me::from)
			.orElseGet(UserResponse.Me::new);
		return Response.success(me, "회원정보를 성공적으로 가져왔습니다.", HttpStatus.OK);
	}

	@UserOrTempAuthorize
	@PostMapping("/profile/img")
	public ResponseEntity<?> updateProfileImg(@CurrentUser CustomUserDetails userDetails,
	                                          @RequestPart("file") MultipartFile multipartFile) {
		if (!ImageUtils.isImage(multipartFile.getContentType())) {
			Response.fail("올바른 이미지형식이 아닙니다", HttpStatus.NOT_ACCEPTABLE);
		}
		return manageUserUseCase.uploadProfileImage(
			userDetails.getUserId(),
			"profile/img/" + userDetails.getProviderTypeAndLoginId(),
			multipartFile)
			? Response.success("프로필 이미지가 업데이트 되었습니다")
			: Response.fail("업로드에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@UserOrTempAuthorize
	@PutMapping("/profile/password")
	public ResponseEntity<?> updatePassword(@CurrentUser CustomUserDetails userDetails,
	                                        @Validated @RequestBody UserRequest.UpdatePassword management) {
		return manageUserUseCase.updatePassword(new UpdatePasswordCommand(userDetails.getUserId(), management.getPassword()))
			? Response.success("비밀번호가 변경되었습니다.")
			: Response.fail("입력정보 또는 회원정보가 잘못되었습니다.", HttpStatus.NOT_ACCEPTABLE);
	}

	@PostMapping("/profile/password")// 잃어버렸을때
	public ResponseEntity<?> updatePassword(@Validated @RequestBody UserRequest.ForgotPassword management) {
		return manageUserUseCase.updatePassword(new UpdatePasswordCommand(management.getDefaultEmail()))
			? Response.success("새로운 비밀번호가 발급되었습니다. 이메일을 확인해주세요.")
			: Response.fail("비밀번호 초기화에 실패하였습니다. 잘못된 회원정보입니다.", HttpStatus.NOT_ACCEPTABLE);
	}

	@GetMapping("/profile/email")
	public ResponseEntity<?> verifyEmail(@RequestParam String code) {// 이메일로 발송된 활성화 주소 검증.
		return manageUserUseCase.verifyEmail(code)
			? Response.success("이메일이 변경되었습니다.")
			: Response.fail("이메일이 만료되었습니다.", HttpStatus.BAD_REQUEST);
	}

	@UserOrTempAuthorize
	@PutMapping("/profile/email")
	public ResponseEntity<?> updateDefaultEmail(@CurrentUser CustomUserDetails userDetails,
	                                            @Validated @RequestBody UserRequest.ChangeEmail userInfo) {
		return manageUserUseCase.updateEmail(
			UpdateEmailCommand.builder()
				.userId(userDetails.getUserId())
				.email(userInfo.getDefaultEmail()).build())
			? Response.success("이메일이 변경되었습니다")
			: Response.fail("이메일 변경에 실패하였습니다.", HttpStatus.BAD_REQUEST);
	}

	@UserOrTempAuthorize
	@PutMapping("/profile")
	public ResponseEntity<?> updateUser(@CurrentUser CustomUserDetails userDetails,
	                                    @Validated @RequestBody UserRequest.Profile profile) {
		UpdateUserCommand command = UpdateUserCommand.builder()
			.userId(userDetails.getUserId())
			.username(profile.getNewUsername())
			.password(profile.getNewPassword())
			.email(profile.getNewEmail()).build();
		if (command.isUsername()) {
			manageUserUseCase.updateUsername(command);
		}
		if (command.isPassword()) {
			manageUserUseCase.updatePassword(command);
		}
		if (!command.isEmail()) {
			return Response.success("변경되었습니다.");
		}
		return manageUserUseCase.updateEmail(UpdateEmailCommand.builder()
			.userId(userDetails.getUserId())
			.email(profile.getNewEmail()).build())
			? Response.success("변경되었습니다.")
			: Response.fail("잘못된 이메일입니다.", HttpStatus.NOT_ACCEPTABLE);
	}

	@UserAuthorize
	@PostMapping("/profile/github")
	public ResponseEntity<?> addGithubAccount(@CurrentUser CustomUserDetails userDetails, HttpServletResponse response) {
		if (userDetails.isGithubEnabled()) {
			return Response.fail("이미 추가된 서비스입니다.", HttpStatus.NOT_ACCEPTABLE);
		}
		manageUserUseCase.addGithubAccount(AddGithubAccountCommand.builder().userId(userDetails.getUserId()).build());
		CookieUtil.addCookie(response, ADD_OAUTH_SERVICE_USER_INFO
			, CookieUtil.serialize(userDetails.getProviderTypeAndLoginId()), ADD_GITHUB_ACCOUNT_REDIS_EXPIRATION);
		return Response.success("서비스가 추가되었습니다.");
	}

	@UserAuthorize
	@PostMapping("/profile/openai")
	public ResponseEntity<?> addOpenAIService(@CurrentUser CustomUserDetails userDetails,
	                                          @Validated @RequestBody UserRequest.OpenAI request) {
		if (userDetails.isOpenAIEnabled()) {
			return Response.fail("이미 추가된 서비스입니다.", HttpStatus.NOT_ACCEPTABLE);
		}
		return manageUserUseCase.updateOpenAIService(UpdateOpenAIServiceCommand.builder()
			.userId(userDetails.getUserId())
			.accessToken(request.getAccessToken()).build())
			? Response.success("서비스가 추가되었습니다.")
			: Response.fail("서비스 추가에 실패하였습니다.", HttpStatus.BAD_REQUEST);
	}

	@GetMapping("/profile/discord")
	public ResponseEntity<?> getDiscordId(@CurrentUser CustomUserDetails userDetails) {
		String discordId = fetchUserInfoUseCase.fetchDiscordIdByUserId(userDetails.getUserId());
		return Response.success(discordId, "추가되었습니다", HttpStatus.OK);
	}

	@PostMapping("/profile/discord")
	public ResponseEntity<?> updateDiscordId(@CurrentUser CustomUserDetails userDetails,
	                                         @RequestBody UserRequest.Discord request) {
		boolean process = manageUserUseCase.updateDiscordId(new DiscordDto(userDetails.getUserId(), request.getDiscordId()));
		return process
			? Response.success("변경되었습니다")
			: Response.fail("디스코드 아이디 업데이트 실패", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@DeleteMapping("/profile/discord")
	public ResponseEntity<?> deleteDiscordId(@CurrentUser CustomUserDetails userDetails) {
		boolean process = manageUserUseCase.updateDiscordId(new DiscordDto(userDetails.getUserId(), null));
		return process
			? Response.success()
			: Response.fail("디스코드 아이디 업데이트 실패", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@UserOrTempAuthorize
	@DeleteMapping
	public ResponseEntity<?> deleteUser(@CurrentUser CustomUserDetails userDetails) {
		manageUserUseCase.deleteUser(DeleteUserCommand.builder().userId(userDetails.getUserId()).build());
		return Response.success("탈퇴되었습니다.");
	}
}
