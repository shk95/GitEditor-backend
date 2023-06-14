package com.shk95.giteditor.web.apis;

import com.shk95.giteditor.domain.application.GithubService;
import com.shk95.giteditor.domain.application.commands.github.*;
import com.shk95.giteditor.domain.common.security.CurrentUser;
import com.shk95.giteditor.domain.common.security.UserAuthorize;
import com.shk95.giteditor.domain.model.github.GithubFile;
import com.shk95.giteditor.domain.model.github.GithubFileMode;
import com.shk95.giteditor.domain.model.github.ServiceUserInfo;
import com.shk95.giteditor.domain.model.user.CustomUserDetails;
import com.shk95.giteditor.utils.Response;
import com.shk95.giteditor.web.apis.request.GithubRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static java.net.URLDecoder.decode;

@Slf4j
@RequestMapping("/git")
@UserAuthorize
@RequiredArgsConstructor
@RestController
public class GithubController {

	private final GithubService githubService;

	@GetMapping("/repos")
	public ResponseEntity<?> getRepos(@CurrentUser CustomUserDetails userDetails,
									  @RequestParam(required = false) String username) throws IOException {
		GetReposCommand command = GetReposCommand.builder().owner(username).build();
		return userDetails.isGithubEnabled()
			? Response.success(githubService.getRepos(ServiceUserInfo.userId(userDetails.getUserEntityId()), command)
			, "리포지토리 목록을 성공적으로 가져왔습니다.", HttpStatus.OK)
			: Response.fail("리포지토리 목록을 가져오는데 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping("/repo")
	public ResponseEntity<?> getRepo(@CurrentUser CustomUserDetails userDetails) throws IOException {
		return userDetails.isGithubEnabled()
			? Response.success(githubService.getRepoInfo(ServiceUserInfo.userId(userDetails.getUserEntityId()))
			, "리포지토리 목록을 성공적으로 가져왔습니다.", HttpStatus.OK)
			: Response.fail("리포지토리 목록을 가져오는데 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping(value = {"/repo/{repoName}/files"})
	public ResponseEntity<?> getFilesFromRoot(@CurrentUser CustomUserDetails userDetails,
											  @PathVariable String repoName,
											  @RequestParam(required = false) String branchName,
											  @RequestParam(required = false) String username) throws IOException {
		GetFilesCommand command = GetFilesCommand.builder()
			.repositoryName(repoName)
			.branchName(decode(branchName, String.valueOf(StandardCharsets.UTF_8)))
			.owner(username)
			.build();
		return userDetails.isGithubEnabled()
			? Response.success(githubService.getFiles(ServiceUserInfo.userId(userDetails.getUserEntityId()), command)
			, "파일 목록을 성공적으로 가져왔습니다.", HttpStatus.OK)
			: Response.fail("파일 목록을 가져오는데 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping(value = {"/repo/{repoName}/tree"})
	public ResponseEntity<?> getFilesByTreeSha(@CurrentUser CustomUserDetails userDetails,
											   @PathVariable String repoName,
											   @RequestParam String branchName,
											   @RequestParam(required = false) String treeSha,
											   @RequestParam(required = false) String recursive,
											   @RequestParam(required = false) String username) throws IOException {
		GetFilesCommand command = GetFilesCommand.builder()
			.repositoryName(repoName)
			.branchName(decode(branchName, String.valueOf(StandardCharsets.UTF_8)))
			.treeSha(treeSha)
			.recursive(recursive != null)
			.owner(username)
			.build();
		return userDetails.isGithubEnabled()
			? Response.success(githubService.getFiles(ServiceUserInfo.userId(userDetails.getUserEntityId()), command)
			, "파일 목록을 성공적으로 가져왔습니다.", HttpStatus.OK)
			: Response.fail("파일 목록을 가져오는데 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping(value = {"/repo/{repoName}/file/string"})
	public ResponseEntity<?> getFileAsBlob(@CurrentUser CustomUserDetails userDetails,
										   @PathVariable String repoName,
										   @RequestParam String sha,
										   @RequestParam String branchName,
										   @RequestParam(required = false) String owner) throws IOException {
		GithubFile fileAsString = githubService.readBlobAsString(ServiceUserInfo.userId(userDetails.getUserEntityId()),
			ReadFileCommand.builder()
				.owner(owner)
				.repoName(repoName)
				.branchName(decode(branchName, String.valueOf(StandardCharsets.UTF_8)))
				.sha(sha)
				.build());
		return Response.success(fileAsString, "파일을 성공적으로 읽었습니다.", HttpStatus.OK);
	}

	@PostMapping("/branch")
	public ResponseEntity<?> createBranch(@CurrentUser CustomUserDetails userDetails,
										  @Validated @RequestBody GithubRequest.CreateBranch request) throws IOException {
		githubService.createBranch(ServiceUserInfo.userId(userDetails.getUserEntityId())
			, CreateBranchCommand.builder()
				.baseBranchName(request.getBaseBranchName())
				.newBranchName(request.getNewBranchName())
				.repoName(request.getRepoName()).build());
		return Response.success("브랜치를 성공적으로 생성하였습니다.");
	}

	@PostMapping("/file")
	public ResponseEntity<?> createFile(@CurrentUser CustomUserDetails userDetails,
										@Validated @RequestBody GithubRequest.File.Create request) throws IOException {
		githubService.createFile(ServiceUserInfo.userId(userDetails.getUserEntityId()),
			CreateFileCommand.builder()
				.repoName(request.getRepoName())
				.branchName(request.getBranchName())
				.path(request.getPath())
				.content(request.getContent())
				.commitMessage(request.getCommitMessage())
				.mode(GithubFileMode.valueOf(request.getMode()))
				.baseTreeSha(request.getBaseTreeSha())
				.build());
		return Response.success("파일을 성공적으로 커밋하였습니다.");
	}
}
