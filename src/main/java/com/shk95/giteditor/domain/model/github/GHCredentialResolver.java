package com.shk95.giteditor.domain.model.github;

import com.shk95.giteditor.domain.common.constant.ProviderType;
import com.shk95.giteditor.domain.model.user.UserId;
import com.shk95.giteditor.domain.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class GHCredentialResolver {

	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public GHCredentialDelegator getCredential(UserId userId) {
		log.info("User's Github Credential Fetched. User Id : [{}]", userId.toString());
		return userRepository.findById(userId)
			.map(u ->
				u.getProviders().stream()
					.filter(provider -> provider.getProviderId().getProviderType() == ProviderType.GITHUB)
					.findFirst()
					.map(provider -> new GHCredentialDelegator(provider.getAccessToken(), provider.getProviderLoginId()))
					.orElseGet(GHCredentialDelegator::new)
			).orElseGet(GHCredentialDelegator::new);
	}
}
