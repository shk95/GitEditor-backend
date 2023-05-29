package com.shk95.giteditor.web.apis;

import com.shk95.giteditor.domain.application.commands.UpdatePasswordCommand;
import com.shk95.giteditor.domain.common.security.CurrentUser;
import com.shk95.giteditor.domain.common.security.UserAuthorize;
import com.shk95.giteditor.domain.model.user.CustomUserDetails;
import com.shk95.giteditor.domain.model.user.UserManagement;
import com.shk95.giteditor.utils.ImageUtils;
import com.shk95.giteditor.utils.Response;
import com.shk95.giteditor.web.apis.request.UserRequest;
import com.shk95.giteditor.web.apis.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserController {

	private final UserManagement userManagement;

	@UserAuthorize
	@Transactional(readOnly = true)
	@GetMapping("/me")
	public ResponseEntity<?> getUser(@CurrentUser CustomUserDetails userDetails) {
		return Response.success(new UserResponse.Me(userDetails), "회원정보를 성공적으로 가져왔습니다.", HttpStatus.OK);
	}

	@UserAuthorize
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

	@UserAuthorize
	@PutMapping("/password")
	public ResponseEntity<?> updatePassword(@CurrentUser CustomUserDetails userDetails,
											@RequestBody UserRequest.Management management) {
		return userManagement.updatePassword(new UpdatePasswordCommand(userDetails.getUserEntityId(), management.getPassword()))
			? Response.success("비밀번호가 변경되었습니다.")
			: Response.fail("입력정보 또는 회원정보가 잘못되었습니다.", HttpStatus.NOT_ACCEPTABLE);
	}

	@PostMapping("/password")// 잃어버렸을때
	public ResponseEntity<?> updatePassword(@RequestBody UserRequest.Management management) {
		return userManagement.updatePassword(new UpdatePasswordCommand(management.getDefaultEmail(), management.getPassword()))
			? Response.success("새로운 비밀번호가 발급되었습니다. 이메일을 확인해주세요.")
			: Response.fail("비밀번호 초기화에 실패하였습니다. 잘못된 회원정보입니다.", HttpStatus.NOT_ACCEPTABLE);
	}

	@PostMapping("/email")
	public ResponseEntity<?> verifyEmail(@RequestParam String code) {// 이메일로 발송된 활성화 주소 검증. 최초 회원가입시 jwt 토큰 코드로 발송.
		return userManagement.verifyEmail(code)
			? Response.success()
			: Response.fail("이메일이 만료되었습니다.", HttpStatus.BAD_REQUEST);
	}
}
