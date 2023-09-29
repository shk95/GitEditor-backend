package com.shk95.giteditor.config;

import com.shk95.giteditor.common.constant.ProviderType;
import com.shk95.giteditor.common.security.Role;
import com.shk95.giteditor.core.user.domain.user.CustomUserDetails;
import com.shk95.giteditor.core.user.domain.user.UserId;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

@Slf4j
public class SpringSecurityAuditorAware implements AuditorAware<UserId> {// TODO:

	@NotNull
	@Override
	public Optional<UserId> getCurrentAuditor() {
		CustomUserDetails currentUser =
			Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
				.map(Authentication::getPrincipal)
				.map(o -> {
					if (o instanceof CustomUserDetails) {
						CustomUserDetails customUserDetails = (CustomUserDetails) o;
						log.info("Current Auditor called : {}", customUserDetails.getUserEntityId());
						return customUserDetails;
					}
					return null;
				}).orElseGet(() -> {
					log.info("Current Auditor called : {}", "ANONYMOUS");
					return CustomUserDetails.builder()
						.userId("ANONYMOUS")
						.defaultUsername("ANONYMOUS")
						.providerType(ProviderType.ANONYMOUS)
						.role(Role.ANONYMOUS)
						.authorities(Collections.singletonList(new SimpleGrantedAuthority(Role.ANONYMOUS.getCode())))
						.defaultEmail("ANONYMOUS")
						.isUserEnabled(false)
						.isUserEmailVerified(false).build();
				});
		UserId userId = currentUser.getUserEntityId();
		log.info("Current Auditor called : {}", userId);
		return Optional.ofNullable(userId);
	}
}
