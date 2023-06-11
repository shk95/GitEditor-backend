package com.shk95.giteditor.config;

import com.shk95.giteditor.domain.model.user.CustomUserDetails;
import com.shk95.giteditor.domain.model.user.UserId;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Slf4j
public class SpringSecurityAuditorAware implements AuditorAware<UserId> {

	@NotNull
	@Override
	public Optional<UserId> getCurrentAuditor() {
		UserId currentUser = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
			.getUserEntityId();
		log.info("Current Auditor called : {}", currentUser);
		return Optional.of(currentUser);
	}
}
