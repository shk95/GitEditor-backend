package com.shk95.giteditor.core.user.adapter.in;

import com.shk95.giteditor.common.security.UserAuthorize;
import com.shk95.giteditor.common.utils.Response;
import com.shk95.giteditor.core.user.application.port.in.FindUserUseCase;
import com.shk95.giteditor.core.user.application.service.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@UserAuthorize
@RequiredArgsConstructor
@RequestMapping("/user/id")
@RestController
public class GetUserController {

	private final FindUserUseCase findUserUseCase;

	/*@GetMapping("/me")
	public ResponseEntity<?> me(@CurrentUser CustomUserDetails userDetails) {
		return Response.success(new UserIdDto(userDetails.getProviderTypeAndLoginId()));
	}*/

	@GetMapping("/{username}")
	public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
		List<UserDto> list = findUserUseCase.getUserListByUsername(username);
		return Response.success(list);
	}

}
