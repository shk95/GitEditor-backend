package com.shk95.giteditor.common.file;

import com.shk95.giteditor.config.ApplicationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class FileStorageResolver {

	private final String activeStorageName;
	private final ApplicationContext applicationContext;

	public FileStorageResolver(ApplicationContext applicationContext, ApplicationProperties properties) {
		this.activeStorageName = properties.getFileStorage().getActive();
		this.applicationContext = applicationContext;
	}

	/**
	 * Resolve the file storage should be used based on
	 * active file storage configuration in application.yml
	 *
	 * @return the active file storage instance
	 */
	public FileStorage resolve() {
		return applicationContext.getBean(activeStorageName, FileStorage.class);
	}
}
