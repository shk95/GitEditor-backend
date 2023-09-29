package com.shk95.giteditor.core.user.domain.user;

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
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class GrantedUserInfo implements UserDetailsService {

	private final UserFinder userFinder;
	private final SecurityUtil securityUtil;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userFinder.find(username)
			.orElseThrow(() -> new UsernameNotFoundException("Cannot find user. user : [" + username + "]"));
		log.info("### login user : [{}]", username);
		return CustomUserDetails.of(user);
	}

	@Transactional(readOnly = true)
	public CustomUserDetails loadUserWithProvider(UserId userId) {
		User user = userFinder.find(userId)
			.orElseThrow(() -> new UsernameNotFoundException("Cannot find user. [" + userId.toString() + "]"));
		log.info("### login user : [{}]", userId.getUserLoginId());
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
