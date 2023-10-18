package com.shk95.giteditor.core.user.adapter.in;

import com.shk95.giteditor.common.utils.Response;
import com.shk95.giteditor.core.user.application.port.in.SearchUserUseCase;
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
@RequiredArgsConstructor
@RequestMapping("/search/user/")
@RestController
public class SearchUserController {

	private final SearchUserUseCase searchUserUseCase;

	@GetMapping("/username/{username}")
	public ResponseEntity<?> searchUserLikeUsername(@PathVariable String username) {
		List<UserDto> list = searchUserUseCase.getUserListLikeUsername(username);
		return Response.success(list);
	}

}
