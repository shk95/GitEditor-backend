package com.shk95.giteditor.domain.application.commands.document;

import com.shk95.giteditor.domain.model.document.Extension;
import com.shk95.giteditor.domain.model.document.StorageType;
import com.shk95.giteditor.domain.model.user.UserId;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateDocumentCommand {

	private UserId userId;
	private String url;

	private String branchName;
	private String baseTreeSha;
	private String commitMessage;

	private StorageType storageType;
	private Extension extension;
	private String repoName;
	private String path;
}
