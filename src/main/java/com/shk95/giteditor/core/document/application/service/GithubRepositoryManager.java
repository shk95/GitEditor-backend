package com.shk95.giteditor.core.document.application.service;

import com.shk95.giteditor.common.ServiceUserId;
import com.shk95.giteditor.core.document.application.port.in.RepositoryServiceUseCase;
import com.shk95.giteditor.core.github.application.service.command.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class GithubRepositoryManager implements RepositoryServiceUseCase {

	@Override
	public void update(ServiceUserId userId, File.UpdateCommand command) {

	}

	@Override
	public void delete(ServiceUserId userId, File.DeleteCommand command) {

	}

	@Override
	public void create(ServiceUserId userId, File.CreateCommand command) {

	}
}
