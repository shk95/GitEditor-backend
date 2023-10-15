package com.shk95.giteditor.core.document.application.port.out;

import com.shk95.giteditor.common.ServiceUserId;
import com.shk95.giteditor.core.document.domain.CreatedFile;

import java.io.IOException;

public interface GithubRepositoryPort {

	CreatedFile save(ServiceUserId userId, CreatedFile createdFile, GithubDto dto) throws IOException;
}
