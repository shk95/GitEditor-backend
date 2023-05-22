package com.shk95.giteditor.config;

import com.shk95.giteditor.web.apis.authenticate.AuthEntryPointImpl;
import com.shk95.giteditor.domain.common.security.filter.JwtAuthenticationFilter;
import com.shk95.giteditor.domain.common.security.handler.LogoutSuccessHandlerImpl;
import com.shk95.giteditor.domain.common.security.handler.OAuth2AuthenticationFailureHandler;
import com.shk95.giteditor.domain.common.security.handler.OAuth2AuthenticationSuccessHandler;
import com.shk95.giteditor.domain.common.security.handler.TokenAccessDeniedHandler;
import com.shk95.giteditor.domain.common.security.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.shk95.giteditor.domain.common.security.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfiguration {

	private final ApplicationProperties properties;
	private final AuthEntryPointImpl authEntryPoint;
	private final LogoutSuccessHandlerImpl logoutSuccessHandler;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final TokenAccessDeniedHandler tokenAccessDeniedHandler;
	private final OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository;
	private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
	private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors().configurationSource(corsConfigurationSource())
			.and()
			.httpBasic().disable().csrf().disable().formLogin().disable()
//			.headers().frameOptions().disable().and()// x-frame 보안
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.exceptionHandling().authenticationEntryPoint(authEntryPoint)// 로그인 인증 실패시 진입점
			.accessDeniedHandler(tokenAccessDeniedHandler)// 사용자 token 인증 실패
			.and()
			.logout().logoutUrl("/auth/logout").logoutSuccessHandler(logoutSuccessHandler)
			.and()
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.authorizeRequests()
			.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
			.antMatchers("/auth/*", "/login/**", "/auth/*/oauth").permitAll()
			.antMatchers("/api/**").hasRole("USER")
			.antMatchers("/admin/**").hasRole("ADMIN")
			.anyRequest().authenticated()
			.and()
			.oauth2Login().authorizationEndpoint().baseUri("/oauth2/authorization")
			.authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository)
			.and()
			.redirectionEndpoint().baseUri("/*/oauth2/code/*")
			.and()
			.userInfoEndpoint().userService(customOAuth2UserService)
			.and()
			.successHandler(oAuth2AuthenticationSuccessHandler)
			.failureHandler(oAuth2AuthenticationFailureHandler);
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	protected CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.addAllowedOriginPattern(properties.getCors().getAllowedOrigins());
		configuration.setAllowedMethods(properties.getCors().getAllowedMethods());
		configuration.setAllowedHeaders(properties.getCors().getAllowedHeaders());
		configuration.setMaxAge(properties.getCors().getMaxAge());
		configuration.setAllowCredentials(true);
		source.registerCorsConfiguration(properties.getCors().getAddMapping(), configuration);
		return source;
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
