package com.shk95.giteditor.core.auth.application.port.out;

import com.shk95.giteditor.core.auth.domain.CustomUserDetails;
import com.shk95.giteditor.core.user.domain.user.UserId;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface LoadUserPort {

	CustomUserDetails loadUser(UserId userId) throws UsernameNotFoundException;

	CustomUserDetails loadUser(String userIdOrEmail) throws UsernameNotFoundException;
}
