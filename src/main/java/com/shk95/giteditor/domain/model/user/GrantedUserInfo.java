package com.shk95.giteditor.domain.model.user;

import com.shk95.giteditor.utils.Response;
import com.shk95.giteditor.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class GrantedUserInfo implements UserDetailsService {

	private final UserFinder userFinder;
	private final SecurityUtil securityUtil;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("### login user : [{}]", username);
		User user = userFinder.find(username)
			.orElseThrow(() -> new UsernameNotFoundException("Cannot find user. user : [" + username + "]"));
		return CustomUserDetails.of(user);
	}

	public CustomUserDetails loadUserWithProvider(UserId userId) {
		User user = userFinder.find(userId)
			.orElseThrow(() -> new UsernameNotFoundException("Cannot find user. [" + userId.toString() + "]"));
		return CustomUserDetails.of(user);
	}

	// test
	public ResponseEntity<?> getAuthorities() {
		// SecurityContext에 담겨 있는 authentication id 정보
		String userId = securityUtil.getCurrentUser();
		User currentUser = userFinder.find(userId).orElseThrow(() -> new UsernameNotFoundException("사용자의 인증정보 없음."));
		return Response.success(currentUser.getRole().getCode(), "User role", HttpStatus.OK);
	}
}
