package com.shk95.giteditor.web.apis;

import com.shk95.giteditor.domain.application.GithubService;
import com.shk95.giteditor.domain.application.commands.GetFilesCommand;
import com.shk95.giteditor.domain.application.commands.GetReposCommand;
import com.shk95.giteditor.domain.common.security.CurrentUser;
import com.shk95.giteditor.domain.common.security.UserAuthorize;
import com.shk95.giteditor.domain.model.github.ServiceUserInfo;
import com.shk95.giteditor.domain.model.user.CustomUserDetails;
import com.shk95.giteditor.utils.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static java.net.URLDecoder.decode;

@Slf4j
@RequestMapping("/git")
@UserAuthorize
@RequiredArgsConstructor
@RestController
public class GitHubController {

	private final GithubService githubService;

	@GetMapping("/repos")
	public ResponseEntity<?> getRepos(@CurrentUser CustomUserDetails userDetails,
	                                  @RequestParam(required = false) String username) throws IOException {
		GetReposCommand command = GetReposCommand.builder().username(username).build();
		return userDetails.isGithubEnabled()
			? Response.success(githubService.getRepos(ServiceUserInfo.userId(userDetails.getUserEntityId()), command)
			, "리포지토리 목록을 성공적으로 가져왔습니다.", HttpStatus.OK)
			: Response.fail("리포지토리 목록을 가져오는데 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping("/repo")
	public ResponseEntity<?> getRepo(@CurrentUser CustomUserDetails userDetails) throws IOException {
		return userDetails.isGithubEnabled()
			? Response.success(githubService.getRepo(ServiceUserInfo.userId(userDetails.getUserEntityId()))
			, "리포지토리 목록을 성공적으로 가져왔습니다.", HttpStatus.OK)
			: Response.fail("리포지토리 목록을 가져오는데 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping(value = {"/repo/{repoName}"})
	public ResponseEntity<?> getFilesFromRoot(@CurrentUser CustomUserDetails userDetails,
	                                          @PathVariable String repoName,
	                                          @RequestParam(required = false) String branchName,
	                                          @RequestParam(required = false) String username) throws IOException {
		GetFilesCommand command = GetFilesCommand.builder()
			.repositoryName(repoName)
			.branchName(decode(branchName, String.valueOf(StandardCharsets.UTF_8)))
			.username(username)
			.build();
		return userDetails.isGithubEnabled()
			? Response.success(githubService.getFilesFromRoot(ServiceUserInfo.userId(userDetails.getUserEntityId()), command)
			, "파일 목록을 성공적으로 가져왔습니다.", HttpStatus.OK)
			: Response.fail("파일 목록을 가져오는데 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping(value = {"/repo/{repoName}/tree"})
	public ResponseEntity<?> getFilesByTreeSha(@CurrentUser CustomUserDetails userDetails,
	                                           @PathVariable String repoName,
	                                           @RequestParam String treeSha,
	                                           @RequestParam String branchName,
	                                           @RequestParam(required = false) String username) throws IOException {
		GetFilesCommand command = GetFilesCommand.builder()
			.repositoryName(repoName)
			.branchName(decode(branchName, String.valueOf(StandardCharsets.UTF_8)))
			.treeSha(treeSha)
			.username(username)
			.build();
		return userDetails.isGithubEnabled()
			? Response.success(githubService.getFilesByTreeSha(ServiceUserInfo.userId(userDetails.getUserEntityId()), command)
			, "파일 목록을 성공적으로 가져왔습니다.", HttpStatus.OK)
			: Response.fail("파일 목록을 가져오는데 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
