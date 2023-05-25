package com.shk95.giteditor.domain.common.file;

import com.shk95.giteditor.config.ApplicationProperties;
import org.springframework.stereotype.Component;

@Component
public class FileUrlCreator {

	private final boolean isLocalStorage;
	private final String cdnUrl;

	public FileUrlCreator(ApplicationProperties properties) {
		this.isLocalStorage = "localFileStorage".equals(properties.getFileStorage().getActive());
		this.cdnUrl = properties.getCdn().getUrl();
	}

	public String url(String fileRelativePath) {
		if (fileRelativePath == null) {
			return null;
		}

		// In case file relative path is actually an URL
		if (fileRelativePath.startsWith("https://") || fileRelativePath.startsWith("http://")) {
			return fileRelativePath;
		}

		// Use local file servlet to serve the file for local dev environment
		if (isLocalStorage) {
			return "/local-file/" + fileRelativePath;
		}
		return cdnUrl + "/" + fileRelativePath;
	}

}
