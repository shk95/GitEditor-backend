package com.shk95.giteditor.core.github.adapter.in;

import com.shk95.giteditor.common.ServiceUserId;
import com.shk95.giteditor.common.security.CurrentUser;
import com.shk95.giteditor.common.security.UserAuthorize;
import com.shk95.giteditor.common.utils.Response;
import com.shk95.giteditor.core.auth.domain.CustomUserDetails;
import com.shk95.giteditor.core.github.application.port.in.GithubServiceUseCase;
import com.shk95.giteditor.core.github.application.service.command.File;
import com.shk95.giteditor.core.github.application.service.command.GetFilesCommand;
import com.shk95.giteditor.core.github.application.service.command.GetReposCommand;
import com.shk95.giteditor.core.github.application.service.command.Repo;
import com.shk95.giteditor.core.github.domain.GithubFile;
import com.shk95.giteditor.core.github.domain.GithubRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static java.net.URLDecoder.decode;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/git/repo")
@UserAuthorize
@RestController
public class GithubRepoController {

	private final GithubServiceUseCase githubServiceUseCase;

	@GetMapping("/readme") // [user/repo] 형식
	public ResponseEntity<?> getRepoReadme(@CurrentUser CustomUserDetails userDetails,
	                                       @RequestParam("repo-name") String repoName) {
		String readme = githubServiceUseCase.getRepoReadme(userDetails.getUserId(), repoName);
		return Response.success((Object) readme);
	}

	@GetMapping
	public ResponseEntity<?> getRepo(@CurrentUser CustomUserDetails userDetails,
	                                 @RequestParam String repoName) throws IOException {
		if (!userDetails.isGithubEnabled()) {
			return Response.fail("권한이 없습니다.", HttpStatus.FORBIDDEN);
		}
		GithubRepo repo = githubServiceUseCase.getRepo(
			ServiceUserId.from(userDetails).get(),
			decode(repoName, String.valueOf(StandardCharsets.UTF_8)));
		return !repo.getRepoName().isEmpty()
			? Response.success(repo, "리포지토리 정보를 가져왔습니다.", HttpStatus.OK)
			: Response.fail("리포지토리 정보가 없습니다.", HttpStatus.NO_CONTENT);
	}

	@Cacheable(value = "repos", key = "#userDetails.providerTypeAndLoginId() + '_' + (#username != null ? #username : 'noUsername')")
	@GetMapping("/all")
	public ResponseEntity<?> getAllRepos(@CurrentUser CustomUserDetails userDetails,
	                                     @RequestParam(required = false) String username) throws IOException {
		if (!userDetails.isGithubEnabled()) {
			return Response.fail("권한이 없습니다.", HttpStatus.FORBIDDEN);
		}
		GetReposCommand command = GetReposCommand.builder().owner(username).build();
		return Response.success(
			githubServiceUseCase.getRepos(
				ServiceUserId.from(userDetails).get(),
				command),
			"리포지토리 목록을 성공적으로 가져왔습니다.",
			HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<?> createRepo(@CurrentUser CustomUserDetails userDetails,
	                                    @RequestBody GithubRequest.CreateRepo request) throws IOException {
		if (!userDetails.isGithubEnabled()) {
			return Response.fail("권한이 없습니다.", HttpStatus.FORBIDDEN);
		}
		String created = githubServiceUseCase.createRepo(
			ServiceUserId.from(userDetails).get(),
			Repo.forCreate(
				request.getRepoName(),
				request.getDescription(),
				request.isMakePrivate()));
		return Response.success(
			GithubResponse.Repo.builder().repoName(created).build(),
			"리포지토리를 성공적으로 생성하였습니다.", HttpStatus.CREATED);
	}

	@DeleteMapping
	public ResponseEntity<?> deleteRepo(@CurrentUser CustomUserDetails userDetails,
	                                    @RequestParam String repoName) throws IOException {
		if (!userDetails.isGithubEnabled()) {
			return Response.fail("권한이 없습니다.", HttpStatus.FORBIDDEN);
		}
		githubServiceUseCase.deleteRepo(
			ServiceUserId.from(userDetails).get(),
			Repo.forDelete(repoName));
		return Response.success("리포지토리를 성공적으로 삭제하였습니다.");
	}

	@GetMapping(value = {"/file/all"})
	public ResponseEntity<?> getFilesFromRoot(@CurrentUser CustomUserDetails userDetails,
	                                          @RequestParam String repoName,
	                                          @RequestParam(required = false) String branchName,
	                                          @RequestParam(required = false) String username) throws IOException {
		if (!userDetails.isGithubEnabled()) {
			return Response.fail("권한이 없습니다.", HttpStatus.FORBIDDEN);
		}
		GetFilesCommand command = GetFilesCommand.builder()
			.repositoryName(repoName)
			.branchName(decode(branchName, String.valueOf(StandardCharsets.UTF_8)))
			.owner(username)
			.build();
		return Response.success(
			githubServiceUseCase.getFiles(
				ServiceUserId.from(userDetails).get(),
				command),
			"파일 목록을 성공적으로 가져왔습니다.",
			HttpStatus.OK);
	}

	/*	@Cacheable(value = "tree",
			key = "#repoName + '_' + #branchName + '_' + (#treeSha != null ? #treeSha : 'noTreeSha') + '_' + (#recursive != null ? #recursive : 'noRecursive') + '_' + (#username != null ? #username : 'noUsername')")*/
	@GetMapping(value = {"/tree"})
	public ResponseEntity<?> getFilesByTreeSha(@CurrentUser CustomUserDetails userDetails,
	                                           @RequestParam String repoName,
	                                           @RequestParam String branchName,
	                                           @RequestParam(required = false) String treeSha,
	                                           @RequestParam(required = false) String recursive,
	                                           @RequestParam(required = false) String username) throws IOException {
		if (!userDetails.isGithubEnabled()) {
			return Response.fail("권한이 없습니다.", HttpStatus.FORBIDDEN);
		}
		GetFilesCommand command = GetFilesCommand.builder()
			.repositoryName(repoName)
			.branchName(decode(branchName, String.valueOf(StandardCharsets.UTF_8)))
			.treeSha(treeSha)
			.recursive(recursive != null)
			.owner(username)
			.build();
		return Response.success(
			githubServiceUseCase.getFiles(
				ServiceUserId.from(userDetails).get(),
				command),
			"파일 목록을 성공적으로 가져왔습니다.",
			HttpStatus.OK);
	}

	@GetMapping(value = {"/file/string"})
	public ResponseEntity<?> getFileAsBlob(@CurrentUser CustomUserDetails userDetails,
	                                       @RequestParam String repoName,
	                                       @RequestParam String sha,
	                                       @RequestParam String branchName,
	                                       @RequestParam(required = false) String owner) throws IOException {
		GithubFile fileAsString = githubServiceUseCase.readBlobAsString(
			ServiceUserId.from(userDetails).get(),
			File.forRead()
				.owner(owner)
				.repoName(repoName)
				.branchName(decode(branchName, String.valueOf(StandardCharsets.UTF_8)))
				.sha(sha)
				.build());
		return Response.success(fileAsString, "파일을 성공적으로 읽었습니다.", HttpStatus.OK);
	}
}
