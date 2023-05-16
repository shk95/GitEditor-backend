package com.shk95.giteditor.domain.model.provider;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProviderRepository extends JpaRepository<Provider, Long> {

	Optional<Provider> findByProviderEmail(String email);
}
