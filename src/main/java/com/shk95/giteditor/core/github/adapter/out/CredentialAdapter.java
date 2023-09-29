package com.shk95.giteditor.core.github.adapter.out;

import com.shk95.giteditor.core.github.application.port.out.GetCredentialPort;
import com.shk95.giteditor.core.github.domain.GithubCredential;
import com.shk95.giteditor.core.github.infrastructure.GithubCredentialResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CredentialAdapter implements GetCredentialPort {

	private final GithubCredentialResolver githubCredentialResolver;

	@Override
	public GithubCredential fetch(String userId) {
		return githubCredentialResolver.fetch(userId);
	}
}
