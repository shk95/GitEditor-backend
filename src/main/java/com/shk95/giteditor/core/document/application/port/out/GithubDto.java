package com.shk95.giteditor.core.document.application.port.out;

import com.shk95.giteditor.core.document.domain.CreatedFile;
import lombok.Builder;

public record GithubDto(
	CreatedFile.StorageType storageType,
	String branchName,
	String baseTreeSha,
	String commitMessage
) {

	@Builder
	public static GithubDto of(CreatedFile.StorageType storageType, String branchName, String baseTreeSha, String commitMessage) {
		return new GithubDto(storageType, branchName, baseTreeSha, commitMessage);
	}
}
