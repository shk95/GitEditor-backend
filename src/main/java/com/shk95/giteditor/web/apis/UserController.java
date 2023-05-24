package com.shk95.giteditor.web.apis;

import com.shk95.giteditor.domain.common.security.CurrentUser;
import com.shk95.giteditor.domain.common.security.UserAuthorize;
import com.shk95.giteditor.domain.model.user.CustomUserDetails;
import com.shk95.giteditor.utils.Response;
import com.shk95.giteditor.web.apis.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@UserAuthorize
@RequestMapping("/user")
@RestController
public class UserController {

	@GetMapping("/me")
	public ResponseEntity<?> getUser(@CurrentUser CustomUserDetails userDetails) {
		return Response.success(new UserResponse.Me(userDetails), "회원정보를 성공적으로 가져왔습니다.", HttpStatus.OK);
	}
}
