package com.shk95.giteditor.config;

import com.shk95.giteditor.domain.common.security.context.AuthEntryPointImpl;
import com.shk95.giteditor.domain.common.security.filter.JwtAuthenticationFilter;
import com.shk95.giteditor.domain.common.security.handler.LogoutSuccessHandlerImpl;
import com.shk95.giteditor.domain.common.security.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
/*
@EnableGlobalMethodSecurity 주석은 Spring에서 메서드 수준 보안을 활성화하는 데 사용됩니다. 메서드 수준 보안의 다양한 측면을 구성하는 세 가지 특성을 제공합니다.

securedEnabled: 메소드 보안을 위해 @Secured 주석을 활성화해야 하는지 여부를 나타냅니다. true로 설정하면 Spring은 보안 상태를 결정하기 위해 메소드에서 @Secured 주석을 찾습니다.
jsr250Enabled: 메서드 보안을 위해 @RolesAllowed 주석을 활성화해야 하는지 여부를 나타냅니다. true로 설정하면 Spring은 보안 상태를 결정하기 위해 메소드에서 @RolesAllowed 주석을 찾습니다.
prePostEnabled: 메서드 보안을 위해 @PreAuthorize 및 @PostAuthorize 주석을 활성화해야 하는지 여부를 나타냅니다. true로 설정하면 Spring은 보안 상태를 결정하기 위해 메서드에서 @PreAuthorize 및 @PostAuthorize 주석을 찾습니다.

prePostEnabled 속성을 true로 설정하면 이 어노테이션은 Spring의 메소드 레벨 보안을 위해 @PreAuthorize 및 @PostAuthorize 어노테이션을 사용할 수 있습니다. 이를 통해 부울 값으로 평가되는 SpEL 표현식을 기반으로 메서드를 보호할 수 있습니다.
예를 들어 @PreAuthorize("hasRole('ROLE_ADMIN')")를 사용하여 "ROLE_ADMIN" 역할이 있는 사용자만 주석이 달린 메서드를 실행할 수 있도록 할 수 있습니다.
 */
@EnableGlobalMethodSecurity(
	// securedEnabled = true,
	// jsr250Enabled = true,
	prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {

	private final AuthEntryPointImpl authEntryPoint;
	private final LogoutSuccessHandlerImpl logoutSuccessHandler;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final AuthenticationConfiguration authenticationConfiguration;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.httpBasic().disable().cors().and().csrf().disable().formLogin().disable()
//			.headers().frameOptions().disable().and()// x-frame 보안

			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
			.authorizeRequests()
			.antMatchers("/auth/*").permitAll()
			.antMatchers("/api/**").hasRole("USER")
			.antMatchers("/admin/**").hasRole("ADMIN")
			.anyRequest().authenticated().and()
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.logout().logoutUrl("/auth/logout").logoutSuccessHandler(logoutSuccessHandler).and()
			.exceptionHandling().authenticationEntryPoint(authEntryPoint).and()
			.oauth2Login().userInfoEndpoint().userService(customOAuth2UserService);


		return http.build();
	}

	/*
	 * auth 매니저 설정
	 * */
	@Bean
	AuthenticationManager authenticationManager() throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
/*
	@Bean
	public OAuth2UserService<OAuth2UserRequest, OAuth2User> githubUserService() {
		return new GithubOAuth2UserService();
	}
*/

	@Bean
	public PasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();
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
