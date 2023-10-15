package com.shk95.giteditor.core.user.adapter.out;

import com.shk95.giteditor.core.user.application.port.out.ProviderRepositoryPort;
import com.shk95.giteditor.core.user.domain.provider.Provider;
import com.shk95.giteditor.core.user.domain.provider.ProviderId;
import com.shk95.giteditor.core.user.infrastructure.JpaProviderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ProviderRepositoryAdapter implements ProviderRepositoryPort {

	private final JpaProviderRepository jpaProviderRepository;

	@Override
	public Provider save(Provider provider) {
		return jpaProviderRepository.save(provider);
	}

	@Override
	public Optional<Provider> findById(ProviderId providerId) {
		return jpaProviderRepository.findById(providerId);
	}

	@Override
	public List<Provider> findAll() {
		return jpaProviderRepository.findAll();
	}
}
