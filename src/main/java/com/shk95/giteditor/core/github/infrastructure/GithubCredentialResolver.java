package com.shk95.giteditor.core.github.infrastructure;

import com.shk95.giteditor.common.ServiceUserId;
import com.shk95.giteditor.common.constant.ProviderType;
import com.shk95.giteditor.core.github.application.service.GithubInitException;
import com.shk95.giteditor.core.github.domain.GithubCredential;
import com.shk95.giteditor.core.user.application.port.out.UserCrudRepositoryPort;
import com.shk95.giteditor.core.user.domain.provider.Provider;
import com.shk95.giteditor.core.user.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
@RequiredArgsConstructor
@Component
public class GithubCredentialResolver {

	private final UserCrudRepositoryPort userCrudRepositoryPort;

	@Transactional(readOnly = true)
	public GithubCredential fetch(String id) {// TODO: 쿼리 최적화
		Predicate<User> isGithubEnabled = User::isGithubEnabled;
		Function<User, Optional<Provider>> findGithubProvider =
			user -> user.getProviders().stream()
				.filter(provider -> provider.getProviderId().getProviderType().equals(ProviderType.GITHUB))
				.findFirst();
		Function<Provider, GithubCredential> toGithubCredential =
			provider -> new GithubCredential(provider.getAccessToken(), provider.getProviderLoginId());

		log.info("User's Github Credential has fetched. Id : [{}]", id);
		return userCrudRepositoryPort.findUserByUserId(ServiceUserId.from(id).userId())
			.filter(isGithubEnabled)
			.flatMap(findGithubProvider)
			.map(toGithubCredential)
			.orElseThrow(GithubInitException::new); // TODO: github credential exception handling
	}
}
