package com.shk95.giteditor.core.auth.adapter.out;

import com.shk95.giteditor.core.auth.application.port.out.LoadUserPort;
import com.shk95.giteditor.core.auth.domain.CustomUserDetails;
import com.shk95.giteditor.core.user.application.port.out.UserCrudRepositoryPort;
import com.shk95.giteditor.core.user.domain.user.User;
import com.shk95.giteditor.core.user.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class LoadUserAdapter implements LoadUserPort {

	private final UserCrudRepositoryPort userCrudRepositoryPort;

	@Override
	public CustomUserDetails loadUser(UserId userId) {
		User user = userCrudRepositoryPort.findUserByUserId(userId)
			.orElseThrow(() -> new UsernameNotFoundException("Cannot find user. [" + userId.toString() + "]"));
		log.info("### User logged in : id [{}]", userId.getUserLoginId());
		return CustomUserDetails.of(user);
	}

	@Override
	public CustomUserDetails loadUser(String userIdOrEmail) throws UsernameNotFoundException {
		User user = userCrudRepositoryPort.findUserByLoginIdOrEmail(userIdOrEmail)
			.orElseThrow(() -> new UsernameNotFoundException("Cannot find user. [" + userIdOrEmail + "]"));
		log.info("### User logged in : id [{}]", user.getUserId());
		return CustomUserDetails.of(user);
	}
}
