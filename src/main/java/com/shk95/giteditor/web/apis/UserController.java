package com.shk95.giteditor.web.apis;

import com.shk95.giteditor.domain.common.security.CurrentUser;
import com.shk95.giteditor.domain.common.security.CustomUserDetails;
import com.shk95.giteditor.domain.common.security.UserAuthorize;
import com.shk95.giteditor.domain.model.provider.Provider;
import com.shk95.giteditor.domain.model.user.User;
import com.shk95.giteditor.domain.model.user.UserRepository;
import com.shk95.giteditor.utils.Response;
import com.shk95.giteditor.web.apis.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@UserAuthorize
@RequestMapping("/user")
@RestController
public class UserController {

	private final UserRepository userRepository;

	//FIXME: 예외처리
	@Transactional
	@GetMapping("/me")
	public ResponseEntity<?> getUser(@CurrentUser CustomUserDetails userDetails) {
		User user = userRepository.findByUserIdAndProviderType(userDetails.getUserId(), userDetails.getProviderType())
			.orElseThrow(RuntimeException::new);
		Provider provider = user.getProviders().stream()
			.filter(entity -> entity.getProviderId().getProviderType() == userDetails.getProviderType())
			.findFirst().orElseThrow(RuntimeException::new);

		UserResponse.Me me = UserResponse.Me.builder()
			.userId(user.getUserId())
			.role(user.getRole())
			.providerType(user.getProviderType())
			.defaultEmail(user.getDefaultEmail())
			.defaultUsername(user.getUsername())
			.defaultImgUrl(user.getProfileImageUrl())
			.providerEmail(provider.getProviderEmail())
			.providerLoginId(provider.getProviderLoginId())
			.providerUsername(provider.getProviderUserName())
			.providerImgUrl(provider.getProviderImgUrl())
			.build();
		return Response.success(me, "회원정보를 성공적으로 가져왔습니다.", HttpStatus.OK);
	}
}
