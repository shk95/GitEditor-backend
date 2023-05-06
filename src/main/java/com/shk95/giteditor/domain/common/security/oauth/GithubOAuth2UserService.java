package com.shk95.giteditor.domain.common.security.oauth;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

public class GithubOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final RestTemplate restTemplate;

	public GithubOAuth2UserService() {
		this.restTemplate = new RestTemplate();
	}

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		String accessToken = userRequest.getAccessToken().getTokenValue();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);
		HttpEntity<Void> entity = new HttpEntity<>(headers);
		ResponseEntity<Map> response = restTemplate.exchange("https://api.github.com/user", HttpMethod.GET, entity,
			Map.class);
		Map<String, Object> userAttributes = response.getBody();
		String name = (String) userAttributes.get("name");
		String email = (String) userAttributes.get("email");
		return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
			userAttributes, "name");
	}
}
