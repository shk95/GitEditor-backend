package com.shk95.giteditor.core.document.application.port.in;

import com.shk95.giteditor.common.ServiceUserId;
import com.shk95.giteditor.core.github.application.service.command.File;

public interface RepositoryServiceUseCase {

	void update(ServiceUserId userId, File.UpdateCommand command);

	void delete(ServiceUserId userId, File.DeleteCommand command);

	void create(ServiceUserId userId, File.CreateCommand command);
}
