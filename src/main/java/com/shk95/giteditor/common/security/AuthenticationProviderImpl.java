package com.shk95.giteditor.common.security;

import com.shk95.giteditor.core.auth.domain.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Deprecated
@RequiredArgsConstructor
public class AuthenticationProviderImpl implements AuthenticationProvider { // default login

	private final PasswordEncoder passwordEncoder;
	private final UserDetailsService userDetailsService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String password = authentication.getCredentials().toString();

		CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
		if (userDetails == null) {
			throw new BadCredentialsException("Authentication failed");
		}
		boolean isEnabled = userDetails.isUserEnabled();
		boolean isUserEmailVerified = userDetails.isUserEmailVerified();

		if (!passwordEncoder.matches(password, userDetails.getPassword())) {
			throw new BadCredentialsException("Authentication failed");
		}
		if (!isEnabled) {
			if (!isUserEmailVerified) {
				throw new DisabledException("User is disabled");
			}
		}
		return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}
