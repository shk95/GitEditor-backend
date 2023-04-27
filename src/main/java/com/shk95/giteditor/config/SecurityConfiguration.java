package com.shk95.giteditor.config;

import com.shk95.giteditor.domain.application.impl.UserServiceImpl;
import com.shk95.giteditor.domain.common.security.jwt.AuthEntryPointJwt;
import com.shk95.giteditor.domain.common.security.jwt.JwtAuthenticationFilter;
import com.shk95.giteditor.domain.common.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
	// securedEnabled = true,
	// jsr250Enabled = true,
	prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {

	private final UserServiceImpl userDetailsService;
	private final JwtTokenProvider jwtTokenProvider;
	private final RedisTemplate redisTemplate;
	private final AuthEntryPointJwt entryPoint;


	@Bean
	public JwtAuthenticationFilter authenticationJwtTokenFilter() {

		return new JwtAuthenticationFilter(jwtTokenProvider, redisTemplate);
	}

/*	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}*/

/*	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}*/

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.httpBasic().disable().cors().and().csrf().disable().formLogin().disable()
//			.logout().disable()
//			.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()

			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.authorizeRequests()
			.antMatchers("/api/*").permitAll()
			.antMatchers("/api/users/**").hasRole("USER")
			.antMatchers("/api/admin/**").hasRole("ADMIN")
			.anyRequest().authenticated()

//		http.authenticationProvider(authenticationProvider());

			.and()
			.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)

			.exceptionHandling().authenticationEntryPoint(entryPoint)
		;
		return http.build();
	}

/*
	@Bean
	public OAuth2UserService<OAuth2UserRequest, OAuth2User> githubUserService() {
		return new GithubOAuth2UserService();
	}
*/

	@Bean
	public PasswordEncoder passwordEncoder() {

		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	/*@Bean
	public InMemoryUserDetailsManager userDetailsService() {

		UserDetails user1 = User.withUsername("user1")
			.password(passwordEncoder().encode("user1Pass"))
			.roles("USER")
			.build();
		UserDetails admin = User.withUsername("admin")
			.password(passwordEncoder().encode("adminPass"))
			.roles("ADMIN")
			.build();
		return new InMemoryUserDetailsManager(user1, admin);
	}*/


}
