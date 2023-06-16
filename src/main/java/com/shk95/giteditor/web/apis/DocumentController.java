package com.shk95.giteditor.web.apis;

import com.shk95.giteditor.domain.application.GithubService;
import com.shk95.giteditor.domain.application.commands.document.CreateDocumentCommand;
import com.shk95.giteditor.domain.application.commands.github.CreateFileCommand;
import com.shk95.giteditor.domain.application.commands.github.DeleteFileCommand;
import com.shk95.giteditor.domain.application.commands.github.UpdateFileCommand;
import com.shk95.giteditor.domain.common.security.CurrentUser;
import com.shk95.giteditor.domain.common.security.UserAuthorize;
import com.shk95.giteditor.domain.model.document.MarkdownService;
import com.shk95.giteditor.domain.model.document.StorageType;
import com.shk95.giteditor.domain.model.github.GithubFileMode;
import com.shk95.giteditor.domain.model.github.ServiceUserInfo;
import com.shk95.giteditor.domain.model.user.CustomUserDetails;
import com.shk95.giteditor.utils.Response;
import com.shk95.giteditor.web.apis.request.DocumentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/document")
@UserAuthorize
@RestController
public class DocumentController {

	private final MarkdownService markdownService;
	private final GithubService githubService;

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
				.commitMessage("giteditor commit")
				.path(request.getBasePath() + "/" + request.getFilename() + ".md").build())) // ex) path : readme -> 확장자 없음
			: Response.fail("Github 연동이 되어있지 않습니다.", HttpStatus.NOT_ACCEPTABLE);
	}

	@DeleteMapping
	public ResponseEntity<?> deleteDocumentOnGithub(@CurrentUser CustomUserDetails userDetails,
													@RequestBody DocumentRequest.Github.Delete request) throws IOException {

		DeleteFileCommand command = DeleteFileCommand.builder()
			.fileSha(request.getFileSha())
			.path(request.getPath())
			.repoName(request.getRepoName())
			.branchName(request.getBranchName())
			.commitMessage("giteditor commit")
			.build();

		GithubFileMode mode = GithubFileMode.fromCode(request.getMode());
		switch (mode) {
			case X_BLOB:
			case RW_BLOB:
				githubService.deleteFile(ServiceUserInfo.userId(userDetails.getUserEntityId()), command);
				break;
			case TREE:
			default:
				return Response.fail("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);
		}
		return Response.success("삭제되었습니다");
	}

	@PutMapping
	public ResponseEntity<?> updateDocumentOnGithub(@CurrentUser CustomUserDetails userDetails,
													@RequestBody DocumentRequest.Github.Content request) throws IOException {
		githubService.updateFile(ServiceUserInfo.userId(userDetails.getUserEntityId()),
			UpdateFileCommand.builder()
				.content(request.getContent())
				.path(request.getPath())
				.repoName(request.getRepoName())
				.branchName(request.getBranchName())
				.commitMessage("giteditor commit")
				.build());
		return Response.success("변경되었습니다.");
	}

	@PostMapping
	public ResponseEntity<?> createDocumentOnGithub(@CurrentUser CustomUserDetails userDetails,
													@RequestBody DocumentRequest.Github.Content request) throws IOException {
		githubService.createFile(ServiceUserInfo.userId(userDetails.getUserEntityId()),
			CreateFileCommand.builder()
				.content(request.getContent())
				.path(request.getPath() + "/" + request.getFilename() + ".md")
				.repoName(request.getRepoName())
				.branchName(request.getBranchName())
				.commitMessage("giteditor commit")
				.build());
		return Response.success("저장되었습니다.");
	}
}
