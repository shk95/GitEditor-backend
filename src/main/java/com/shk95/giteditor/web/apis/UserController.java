package com.shk95.giteditor.web.apis;

import com.shk95.giteditor.domain.application.commands.*;
import com.shk95.giteditor.domain.common.security.CurrentUser;
import com.shk95.giteditor.domain.common.security.UserAuthorize;
import com.shk95.giteditor.domain.common.security.UserOrTempAuthorize;
import com.shk95.giteditor.domain.model.user.CustomUserDetails;
import com.shk95.giteditor.domain.model.user.UserManagement;
import com.shk95.giteditor.utils.CookieUtil;
import com.shk95.giteditor.utils.ImageUtils;
import com.shk95.giteditor.utils.Response;
import com.shk95.giteditor.web.apis.request.UserRequest;
import com.shk95.giteditor.web.apis.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

import static com.shk95.giteditor.config.ConstantFields.ADD_GITHUB_ACCOUNT_REDIS_EXPIRATION;
import static com.shk95.giteditor.config.ConstantFields.ADD_OAUTH_SERVICE_USER_INFO;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserController {

	private final UserManagement userManagement;

	@UserOrTempAuthorize
	@Transactional(readOnly = true)
	@GetMapping("/me")
	public ResponseEntity<?> me(@CurrentUser CustomUserDetails userDetails) {
		return Response.success(new UserResponse.Me(userDetails), "회원정보를 성공적으로 가져왔습니다.", HttpStatus.OK);
	}

	@UserOrTempAuthorize
	@PostMapping("/profile/img")
	public ResponseEntity<?> updateProfileImg(@CurrentUser CustomUserDetails userDetails,
											  @RequestPart("file") MultipartFile multipartFile) {
		if (!ImageUtils.isImage(multipartFile.getContentType())) {
			Response.fail("올바른 이미지형식이 아닙니다", HttpStatus.NOT_ACCEPTABLE);
		}
		return userManagement.uploadProfileImage(userDetails.getUserEntityId()
			, "profile/img/" + userDetails.getUserEntityId().get(), multipartFile)
			? Response.success("프로필 이미지가 업데이트 되었습니다")
			: Response.fail("업로드에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@UserOrTempAuthorize
	@PutMapping("/password")
	public ResponseEntity<?> updatePassword(@CurrentUser CustomUserDetails userDetails,
											@Validated @RequestBody UserRequest.UpdatePassword management) {
		return userManagement.updatePassword(new UpdatePasswordCommand(userDetails.getUserEntityId(), management.getPassword()))
			? Response.success("비밀번호가 변경되었습니다.")
			: Response.fail("입력정보 또는 회원정보가 잘못되었습니다.", HttpStatus.NOT_ACCEPTABLE);
	}

	@PostMapping("/password")// 잃어버렸을때
	public ResponseEntity<?> updatePassword(@Validated @RequestBody UserRequest.ForgotPassword management) {
		return userManagement.updatePassword(new UpdatePasswordCommand(management.getDefaultEmail()))
			? Response.success("새로운 비밀번호가 발급되었습니다. 이메일을 확인해주세요.")
			: Response.fail("비밀번호 초기화에 실패하였습니다. 잘못된 회원정보입니다.", HttpStatus.NOT_ACCEPTABLE);
	}

	@GetMapping("/email")
	public ResponseEntity<?> verifyEmail(@RequestParam String code) {// 이메일로 발송된 활성화 주소 검증.
		return userManagement.verifyEmail(code)
			? Response.success("이메일이 변경되었습니다.")
			: Response.fail("이메일이 만료되었습니다.", HttpStatus.BAD_REQUEST);
	}

	@UserOrTempAuthorize
	@PutMapping("/email")
	public ResponseEntity<?> updateDefaultEmail(@CurrentUser CustomUserDetails userDetails,
												@Validated @RequestBody UserRequest.ChangeEmail userInfo) {
		return userManagement.updateEmail(UpdateEmailCommand.builder()
			.userId(userDetails.getUserEntityId())
			.email(userInfo.getDefaultEmail()).build())
			? Response.success("이메일이 변경되었습니다")
			: Response.fail("이메일 변경에 실패하였습니다.", HttpStatus.BAD_REQUEST);
	}

	@UserOrTempAuthorize
	@PutMapping("/profile")
	public ResponseEntity<?> updateUser(@CurrentUser CustomUserDetails userDetails,
										@Validated @RequestBody UserRequest.Profile profile) {
		UpdateUserCommand command = UpdateUserCommand.builder()
			.userId(userDetails.getUserEntityId())
			.username(profile.getNewUsername())
			.password(profile.getNewPassword())
			.email(profile.getNewEmail()).build();
		if (command.isUsername()) {
			userManagement.updateUsername(command);
		}
		if (command.isPassword()) {
			userManagement.updatePassword(command);
		}
		if (!command.isEmail()) {
			return Response.success("변경되었습니다.");
		}
		return userManagement.updateEmail(UpdateEmailCommand.builder()
			.userId(userDetails.getUserEntityId())
			.email(profile.getNewEmail()).build())
			? Response.success("변경되었습니다.")
			: Response.fail("잘못된 이메일입니다.", HttpStatus.NOT_ACCEPTABLE);
	}

	@UserOrTempAuthorize
	@DeleteMapping
	public ResponseEntity<?> deleteUser(@CurrentUser CustomUserDetails userDetails) {
		userManagement.deleteUser(DeleteUserCommand.builder().userId(userDetails.getUserEntityId()).build());
		return Response.success("탈퇴되었습니다.");
	}

	@UserAuthorize
	@PostMapping("/profile/github")
	public ResponseEntity<?> addGithubAccount(@CurrentUser CustomUserDetails userDetails, HttpServletResponse response) {
		if (userDetails.isGithubEnabled()) {
			return Response.fail("이미 추가된 서비스입니다.", HttpStatus.NOT_ACCEPTABLE);
		}
		userManagement.addGithubAccount(AddGithubAccountCommand.builder().userId(userDetails.getUserEntityId()).build());
		CookieUtil.addCookie(response, ADD_OAUTH_SERVICE_USER_INFO
			, CookieUtil.serialize(userDetails.getUserEntityId().get()), ADD_GITHUB_ACCOUNT_REDIS_EXPIRATION);
		return Response.success("서비스가 추가되었습니다.");
	}

	@UserAuthorize
	@PostMapping("/profile/openai")
	public ResponseEntity<?> addOpenAIService(@CurrentUser CustomUserDetails userDetails,
											  @Validated @RequestBody UserRequest.OpenAI request) {
		if (userDetails.isOpenAIEnabled()) {
			return Response.fail("이미 추가된 서비스입니다.", HttpStatus.NOT_ACCEPTABLE);
		}
		return userManagement.updateOpenAIService(UpdateOpenAIServiceCommand.builder()
			.userId(userDetails.getUserEntityId())
			.accessToken(request.getAccessToken()).build())
			? Response.success("서비스가 추가되었습니다.")
			: Response.fail("서비스 추가에 실패하였습니다.", HttpStatus.BAD_REQUEST);
	}
}
