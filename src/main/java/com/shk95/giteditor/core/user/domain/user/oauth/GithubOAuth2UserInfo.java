package com.shk95.giteditor.core.user.domain.user.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shk95.giteditor.common.constant.ProviderType;
import com.shk95.giteditor.common.model.AbstractOAuth2UserInfo;
import io.jsonwebtoken.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;
import java.util.stream.StreamSupport;

import static com.shk95.giteditor.config.Constants.OAuthService.PROVIDER_ACCESS_TOKEN;

@Slf4j
public class GithubOAuth2UserInfo extends AbstractOAuth2UserInfo {

	public GithubOAuth2UserInfo(Map<String, Object> attributes, Map<String, String> additionalAttributes, ProviderType providerType) {
		super(attributes, additionalAttributes, providerType);
	}

	private static String restWrapper(String url, HttpHeaders headers) {
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
		return super.getAttributes().get("id").toString();
	}

	@Override
	public String getLoginId() {
		return (String) super.getAttributes().get("login");
	}

	@Override
	public String getName() {
		String name = (String) super.getAttributes().get("name");
		return name == null ? "" : name;
	}

	@Override
	public String getEmail() {
		String url = "https://api.github.com/user/emails";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/vnd.github+json");
		headers.set("Authorization", "Bearer " + super.getAdditionalAttributes().get(PROVIDER_ACCESS_TOKEN));
		headers.set("X-GitHub-Api-Version", "2022-11-28");

		String response = GithubOAuth2UserInfo.restWrapper(url, headers);

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode;
		String email;
		try {
			rootNode = objectMapper.readTree(response);
			email = StreamSupport.stream(rootNode.spliterator(), false)
				.filter(emailNode -> emailNode.get("primary").asBoolean())
				.findFirst()
				.map(emailNode -> emailNode.get("email").asText()).orElseThrow(JsonParseException::new);
		} catch (Exception e) {
			throw new RuntimeException(e);// TODO: gitOauthUserinfo: 유저 정보 가져오기 json 파싱 중 나타날수있는 예외처리
		}
		Assert.notNull(email, "The value of `email` may not be null");
		log.debug("@@@@ {}. email value : [{}]", this.getClass().getName(), email);
		return email;
	}

	@Override
	public String getImageUrl() {
		return (String) super.getAttributes().get("avatar_url");
	}

	@Override
	public String getAccessToken() {
		return super.getAdditionalAttributes().get(PROVIDER_ACCESS_TOKEN);
	}
}
