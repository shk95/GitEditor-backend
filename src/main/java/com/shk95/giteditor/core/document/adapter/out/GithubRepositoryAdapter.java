package com.shk95.giteditor.core.document.adapter.out;

import com.shk95.giteditor.common.ServiceUserId;
import com.shk95.giteditor.core.document.application.port.out.GithubDto;
import com.shk95.giteditor.core.document.application.port.out.GithubRepositoryPort;
import com.shk95.giteditor.core.document.domain.CreatedFile;
import com.shk95.giteditor.core.github.application.port.in.GithubServiceUseCase;
import com.shk95.giteditor.core.github.application.service.command.File;
import com.shk95.giteditor.core.github.domain.GithubFileMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class GithubRepositoryAdapter implements GithubRepositoryPort {

	private final GithubServiceUseCase githubServiceUseCase;

	@Override
	public CreatedFile save(ServiceUserId userId, CreatedFile createdFile, GithubDto dto) throws IOException {
		githubServiceUseCase.createFile(userId.get(),
			File.forCreate()
				.content(createdFile.getDocument().getContent())
				.repoName(createdFile.getRepoName())
				.path(createdFile.getPath())
				.branchName(dto.branchName())
				.baseTreeSha(dto.baseTreeSha())
				.commitMessage(dto.commitMessage())
				.mode(GithubFileMode.RW_BLOB).build());
		return createdFile;
	}
}
