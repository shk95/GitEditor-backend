package com.shk95.giteditor.core.auth.infrastructure;

import com.shk95.giteditor.core.auth.application.port.out.LoadUserPort;
import com.shk95.giteditor.core.auth.domain.CustomUserDetails;
import com.shk95.giteditor.core.user.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

	private final LoadUserPort loadUserPort;

	@Override
	public UserDetails loadUserByUsername(String providerTypeAndUserId) throws UsernameNotFoundException {
		UserId userId = UserId.of(providerTypeAndUserId);
		CustomUserDetails customUserDetails = loadUserPort.loadUser(userId);
		log.info("### Load User : [{}]", userId);
		return customUserDetails;
	}
}
