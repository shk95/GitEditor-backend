package com.shk95.giteditor.core.document.application.service;

import com.shk95.giteditor.common.ServiceUserId;
import com.shk95.giteditor.core.document.application.port.out.GithubDto;
import com.shk95.giteditor.core.document.domain.CreatedFile;
import lombok.Builder;

public record CreateDocumentDto(
	ServiceUserId userId,
	String url,

	String repoName,
	String path,

	CreatedFile.StorageType storageType,
	String branchName,
	String baseTreeSha,
	String commitMessage
) {

	public GithubDto toGithubDto() {
		return GithubDto.builder()
			.storageType(this.storageType)
			.branchName(this.branchName)
			.baseTreeSha(this.baseTreeSha)
			.commitMessage(this.commitMessage)
			.build();
	}

	@Builder
	public static CreateDocumentDto of(
		ServiceUserId userId,
		String url,
		String repoName,
		String path,
		CreatedFile.StorageType storageType,
		String branchName,
		String baseTreeSha,
		String commitMessage
	) {
		return new CreateDocumentDto(userId, url, repoName, path, storageType, branchName, baseTreeSha, commitMessage);
	}
}
