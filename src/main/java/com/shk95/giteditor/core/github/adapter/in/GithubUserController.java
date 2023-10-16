package com.shk95.giteditor.core.github.adapter.in;

import com.shk95.giteditor.common.security.CurrentUser;
import com.shk95.giteditor.common.security.UserAuthorize;
import com.shk95.giteditor.common.utils.Response;
import com.shk95.giteditor.core.auth.domain.CustomUserDetails;
import com.shk95.giteditor.core.github.application.port.in.GetUserInfoUseCase;
import com.shk95.giteditor.core.github.application.service.dto.PageInfo;
import com.shk95.giteditor.core.github.domain.GithubRepo;
import com.shk95.giteditor.core.github.domain.GithubUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/git/user")
@UserAuthorize
@RestController
public class GithubUserController {

	private final GetUserInfoUseCase getUserInfoUseCase;

	@GetMapping("/following/all")
	public ResponseEntity<?> getAllUserFollowsInfo(@CurrentUser CustomUserDetails userDetails) {
		List<GithubUser> list = getUserInfoUseCase.getUserFollowsSimpleList(userDetails.getUserId());
		return Response.success(list);
	}

	@GetMapping("/follower/all")
	public ResponseEntity<?> getAllFollowersInfo(@CurrentUser CustomUserDetails userDetails) {
		List<GithubUser> list = getUserInfoUseCase.getFollowersSimpleList(userDetails.getUserId());
		return Response.success(list);
	}

	@GetMapping("/bio/{user-id}")
	public ResponseEntity<?> getUserBio(@CurrentUser CustomUserDetails userDetails,
	                                    @PathVariable("user-id") String userLogin) {
		String bio = getUserInfoUseCase.getUserBio(userDetails.getUserId(), userLogin);
		return Response.success(bio);
	}

	@GetMapping("/bio")
	public ResponseEntity<?> getMyBio(@CurrentUser CustomUserDetails userDetails) {
		String bio = getUserInfoUseCase.getUserBio(userDetails.getUserId(), null);
		return Response.success(bio);
	}

	@GetMapping("/stars/all")
	public ResponseEntity<?> getMyStarsList(@CurrentUser CustomUserDetails userDetails) {
		List<GithubRepo> list = getUserInfoUseCase.getAllStarredRepoSimpleList(userDetails.getUserId(), null);
		return Response.success(list);
	}

	@GetMapping("/stars")
	public ResponseEntity<?> getMyStarsList(@CurrentUser CustomUserDetails userDetails,
	                                        @RequestParam(value = "page-at", defaultValue = "1") int pageAt,
	                                        @RequestParam(value = "page-size", defaultValue = "10") int pageSize) {
		List<GithubRepo> list = getUserInfoUseCase.getAllStarredRepoSimpleList(
			userDetails.getUserId(),
			new PageInfo(pageAt, pageSize));
		return Response.success(list);
	}
}
