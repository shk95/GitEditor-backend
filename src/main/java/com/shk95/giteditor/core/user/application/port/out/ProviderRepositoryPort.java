package com.shk95.giteditor.core.user.application.port.out;

import com.shk95.giteditor.core.user.domain.provider.Provider;
import com.shk95.giteditor.core.user.domain.provider.ProviderId;

import java.util.List;
import java.util.Optional;

public interface ProviderRepositoryPort {

	Provider save(Provider provider);

	Optional<Provider> findById(ProviderId providerId);

	List<Provider> findAll();
}
