package com.shk95.giteditor.core.github.application.port.out;

import com.shk95.giteditor.core.github.domain.GithubCredential;

public interface GetCredentialPort {

	GithubCredential fetch(String userId);
}
