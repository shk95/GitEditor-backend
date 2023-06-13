package com.shk95.giteditor.web.apis;

import com.shk95.giteditor.domain.application.commands.document.CreateDocumentCommand;
import com.shk95.giteditor.domain.common.security.CurrentUser;
import com.shk95.giteditor.domain.common.security.UserAuthorize;
import com.shk95.giteditor.domain.model.document.Extension;
import com.shk95.giteditor.domain.model.document.MarkdownService;
import com.shk95.giteditor.domain.model.document.StorageType;
import com.shk95.giteditor.domain.model.user.CustomUserDetails;
import com.shk95.giteditor.utils.Response;
import com.shk95.giteditor.web.apis.request.DocumentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/document")
@UserAuthorize
@RestController
public class DocumentController {

	private final MarkdownService markdownService;

	@PostMapping("/github/markdown")
	public ResponseEntity<?> createDocumentAsMarkdownOnGithub(@CurrentUser CustomUserDetails userDetails,
															  @RequestBody DocumentRequest.Markdown.Create request) throws IOException {
		return userDetails.isGithubEnabled()
			? Response.success(markdownService.createMarkdown(
			CreateDocumentCommand.builder()
				.userId(userDetails.getUserEntityId())
				.url(request.getUrl())
				.storageType(StorageType.GITHUB)
				.repoName(request.getRepoName())
				.branchName(request.getBranchName())
				.baseTreeSha(request.getBaseTreeSha())
				.path(request.getPath())
				.fileName(request.getFileName())
				.extension(Extension.MD).build()))
			: Response.fail("Github 연동이 되어있지 않습니다.", HttpStatus.NOT_ACCEPTABLE);
	}
}
