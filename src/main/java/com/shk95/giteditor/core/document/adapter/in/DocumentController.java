package com.shk95.giteditor.core.document.adapter.in;

import com.shk95.giteditor.common.ServiceUserId;
import com.shk95.giteditor.common.security.CurrentUser;
import com.shk95.giteditor.common.security.UserAuthorize;
import com.shk95.giteditor.core.document.application.port.in.CrawlAndSaveUseCase;
import com.shk95.giteditor.core.document.application.port.in.RepositoryServiceUseCase;
import com.shk95.giteditor.core.document.application.service.CreateDocumentDto;
import com.shk95.giteditor.core.document.domain.CreatedFile;
import com.shk95.giteditor.core.github.application.service.command.File;
import com.shk95.giteditor.core.github.domain.GithubFileMode;
import com.shk95.giteditor.core.user.domain.user.CustomUserDetails;
import com.shk95.giteditor.utils.Response;
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

	private final CrawlAndSaveUseCase crawlAndSaveUseCase;
	private final RepositoryServiceUseCase repositoryServiceUseCase;

	@PostMapping("/github/markdown")
	public ResponseEntity<?> createDocumentAsMarkdownOnGithub(@CurrentUser CustomUserDetails userDetails,
	                                                          @RequestBody DocumentRequest.Markdown.Create request) throws IOException {
		if (!userDetails.isGithubEnabled()) {
			Response.fail("Github 연동이 되어있지 않습니다.", HttpStatus.FORBIDDEN);
		}
		return Response.success(crawlAndSaveUseCase.asMarkdown(
			CreateDocumentDto.builder()
				.userId(ServiceUserId.from(userDetails))
				.url(request.getUrl())
				.storageType(CreatedFile.StorageType.GITHUB)
				.repoName(request.getRepoName())
				.branchName(request.getBranchName())
				.baseTreeSha(request.getBaseTreeSha())
				.commitMessage("giteditor commit")
				.path(request.getBasePath() + "/" + request.getFilename() + ".md").build())); // ex) path : readme -> 확장자 없음
	}

	@DeleteMapping //TODO: 주소 "/github" 추가
	public ResponseEntity<?> deleteDocumentOnGithub(@CurrentUser CustomUserDetails userDetails,
	                                                @RequestBody DocumentRequest.Github.Delete request) throws IOException {
		if (!userDetails.isGithubEnabled()) {
			Response.fail("Github 연동이 되어있지 않습니다.", HttpStatus.FORBIDDEN);
		}
		File.DeleteCommand command = File.forDelete()
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
				repositoryServiceUseCase.delete(ServiceUserId.from(userDetails), command);
				break;
			case TREE:
			default:
				return Response.fail("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);
		}
		return Response.success("삭제되었습니다");
	}

	@PutMapping //TODO: 주소 "/github" 추가
	public ResponseEntity<?> updateDocumentOnGithub(@CurrentUser CustomUserDetails userDetails,
	                                                @RequestBody DocumentRequest.Github.Content request) throws IOException {
		if (!userDetails.isGithubEnabled()) {
			Response.fail("Github 연동이 되어있지 않습니다.", HttpStatus.FORBIDDEN);
		}
		repositoryServiceUseCase.update(ServiceUserId.from(userDetails),
			File.forUpdate()
				.content(request.getContent())
				.path(request.getPath())
				.repoName(request.getRepoName())
				.branchName(request.getBranchName())
				.commitMessage("giteditor commit")
				.build());
		return Response.success("변경되었습니다.");
	}

	@PostMapping //TODO: 주소 "/github" 추가
	public ResponseEntity<?> createDocumentOnGithub(@CurrentUser CustomUserDetails userDetails,
	                                                @RequestBody DocumentRequest.Github.Content request) throws IOException {
		if (!userDetails.isGithubEnabled()) {
			Response.fail("Github 연동이 되어있지 않습니다.", HttpStatus.FORBIDDEN);
		}
		repositoryServiceUseCase.create(ServiceUserId.from(userDetails),
			File.forCreate()
				.content(request.getContent())
				.path(request.getPath() + "/" + request.getFilename() + ".md")
				.repoName(request.getRepoName())
				.branchName(request.getBranchName())
				.commitMessage("giteditor commit")
				.build());
		return Response.success("저장되었습니다.");
	}
}
