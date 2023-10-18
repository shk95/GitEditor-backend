package com.shk95.giteditor.config;

import com.shk95.giteditor.common.authenticate.AuthEntryPointImpl;
import com.shk95.giteditor.common.security.filter.JwtAuthenticationFilter;
import com.shk95.giteditor.common.security.handler.OAuth2AuthenticationFailureHandler;
import com.shk95.giteditor.common.security.handler.OAuth2AuthenticationSuccessHandler;
import com.shk95.giteditor.common.security.handler.TokenAccessDeniedHandler;
import com.shk95.giteditor.common.security.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.shk95.giteditor.common.security.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfiguration {

	private final ApplicationProperties properties;
	private final AuthEntryPointImpl authEntryPoint;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final TokenAccessDeniedHandler tokenAccessDeniedHandler;
	private final OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository;
	private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
	private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors(a -> corsConfigurationSource())
			.httpBasic(AbstractHttpConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
//			.headers().frameOptions().disable().and()// x-frame 보안
			.sessionManagement(a -> a.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.exceptionHandling(a -> a
				.authenticationEntryPoint(authEntryPoint)
				.accessDeniedHandler(tokenAccessDeniedHandler))// 사용자 token 인증 실패
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.authorizeHttpRequests(a -> a
				.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
				.requestMatchers(antMatcher("/auth/login")).permitAll()
				.requestMatchers(antMatcher("/auth/logout")).permitAll()
				.requestMatchers(antMatcher("/auth/reissue")).permitAll()
				.requestMatchers(antMatcher("/user/signup")).permitAll()
				.requestMatchers(antMatcher("/user/signup/oauth")).permitAll()
				.requestMatchers(antMatcher("/user/profile/password")).permitAll()
				.requestMatchers(antMatcher("/user/profile/email")).permitAll()
				.requestMatchers(antMatcher("/search/**")).permitAll()
				.requestMatchers(antMatcher("/api/**")).hasRole("USER")
				.requestMatchers(antMatcher("/admin/**")).hasRole("ADMIN")
				.anyRequest().authenticated())
//				.anyRequest().permitAll()) // test
			.oauth2Login(a ->
				a.authorizationEndpoint(endpoint -> endpoint
						.baseUri("/oauth2/authorization")
						.authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository)
					).redirectionEndpoint(endpoint -> endpoint.baseUri("/*/oauth2/code/*"))
					.userInfoEndpoint(endpoint -> endpoint.userService(customOAuth2UserService))
					.successHandler(oAuth2AuthenticationSuccessHandler)
					.failureHandler(oAuth2AuthenticationFailureHandler));
		return http.getOrBuild();
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
}
