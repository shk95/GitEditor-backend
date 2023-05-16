package com.shk95.giteditor.domain.common.security.info.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shk95.giteditor.domain.common.security.info.OAuth2UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

import static com.shk95.giteditor.config.ConstantFields.OAuthService.PROVIDER_ACCESS_TOKEN;

@Slf4j
public class GithubOAuth2UserInfo extends OAuth2UserInfo {

	private final String accessToken = super.getAdditionalAttributes().get(PROVIDER_ACCESS_TOKEN);

	public GithubOAuth2UserInfo(Map<String, Object> attributes, Map<String, String> additionalAttributes) {
		super(attributes, additionalAttributes);
	}

	private String restWrapper(String url, HttpHeaders headers) {
		RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

		String responseBody = "";
		if (response.getStatusCode().is2xxSuccessful()) {
			responseBody = response.getBody();
		} else {
			log.error("Request failed with status code: " + response.getStatusCode());
		}
		return responseBody;
	}

	@Override
	public String getId() {
		return (String) super.attributes.get("login");
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public String getEmail() {
		String url = "https://api.github.com/user/emails";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/vnd.github+json");
		headers.set("Authorization", "Bearer " + accessToken);
		headers.set("X-GitHub-Api-Version", "2022-11-28");
		String response = this.restWrapper(url, headers);

		String email = "";
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			JsonNode rootNode = objectMapper.readTree(response);
			for (JsonNode emailNode : rootNode) {
				boolean primary = emailNode.get("primary").asBoolean();
				if (primary) {
					email = emailNode.get("email").asText();
					break;
				}
			}
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);// TODO: gitOauthUserinfo: 유저 정보 가져오기 json 파싱 중 나타날수있는 예외처리
		}
		log.debug("@@@@ {}. email value : [{}]", this.getClass().getName(), email);
		return email;
	}

	@Override
	public String getImageUrl() {
		return (String) super.attributes.get("avatar_url");
	}
}
