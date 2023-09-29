package com.shk95.giteditor.core.github.infrastructure;

import com.shk95.giteditor.common.ServiceUserId;
import com.shk95.giteditor.common.constant.ProviderType;
import com.shk95.giteditor.core.github.application.service.GithubInitException;
import com.shk95.giteditor.core.github.domain.GithubCredential;
import com.shk95.giteditor.core.user.domain.user.User;
import com.shk95.giteditor.core.user.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class GithubCredentialResolver {

	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public GithubCredential fetch(String id) {// TODO: 쿼리 최적화
		log.info("User's Github Credential has fetched. Id : [{}]", id);
		return userRepository.findById(ServiceUserId.from(id).userId())
			.filter(User::isGithubEnabled)
			.map(u -> u.getProviders().stream()
				.filter(provider -> provider.getProviderId().getProviderType().equals(ProviderType.GITHUB))
				.findFirst().map(provider -> new GithubCredential(provider.getAccessToken(), provider.getProviderLoginId()))
				.orElseThrow(GithubInitException::new)).orElseThrow(GithubInitException::new);// TODO: github credential exception handling
	}
}
