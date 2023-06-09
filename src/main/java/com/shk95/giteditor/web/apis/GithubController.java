package com.shk95.giteditor.web.apis;

import com.shk95.giteditor.domain.application.GithubService;
import com.shk95.giteditor.domain.application.commands.github.*;
import com.shk95.giteditor.domain.common.security.CurrentUser;
import com.shk95.giteditor.domain.common.security.UserAuthorize;
import com.shk95.giteditor.domain.model.github.GithubFile;
import com.shk95.giteditor.domain.model.github.GithubRepo;
import com.shk95.giteditor.domain.model.github.ServiceUserInfo;
import com.shk95.giteditor.domain.model.user.CustomUserDetails;
import com.shk95.giteditor.utils.Response;
import com.shk95.giteditor.web.apis.request.GithubRequest;
import com.shk95.giteditor.web.apis.response.GithubResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
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

	@Cacheable(value = "repos", key = "#userDetails.getUserEntityId() + '_' + (#username != null ? #username : 'noUsername')")
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
	public ResponseEntity<?> getRepo(@CurrentUser CustomUserDetails userDetails,
									 @RequestParam String repoName) throws IOException {
		GithubRepo repo = githubService.getRepoInfo(
			ServiceUserInfo.userId(userDetails.getUserEntityId()),
			decode(repoName, String.valueOf(StandardCharsets.UTF_8)));
		return userDetails.isGithubEnabled() && !repo.getRepoName().isEmpty()
			? Response.success(repo, "리포지토리 정보를 가져왔습니다.", HttpStatus.OK)
			: Response.fail("리포지토리 정보를 가져오는데 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
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

	/*	@Cacheable(value = "tree",
			key = "#repoName + '_' + #branchName + '_' + (#treeSha != null ? #treeSha : 'noTreeSha') + '_' + (#recursive != null ? #recursive : 'noRecursive') + '_' + (#username != null ? #username : 'noUsername')")*/
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

	@DeleteMapping("/branch")
	public ResponseEntity<?> deleteBranch(@CurrentUser CustomUserDetails userDetails,
										  @Validated @RequestBody GithubRequest.DeleteBranch request) throws IOException {
		githubService.deleteBranch(ServiceUserInfo.userId(userDetails.getUserEntityId())
			, DeleteBranchCommand.builder()
				.branchName(request.getBranchName())
				.repoName(request.getRepoName()).build());
		return Response.success("브랜치를 성공적으로 삭제하였습니다.");
	}

	/*@PostMapping("/file")// 문서 1개 생성
	public ResponseEntity<?> createFile(@CurrentUser CustomUserDetails userDetails,
										@Validated @RequestBody GithubRequest.File.Create request) throws IOException {
		githubService.createFile(ServiceUserInfo.userId(userDetails.getUserEntityId()),
			CreateFileCommand.builder()
				.repoName(request.getRepoName())
				.branchName(request.getBranchName())
				.basePath(request.getPath())// ex) 기본적으로 파일이 위치하는 경로
				.content(request.getContent())
				.filename(request.getFilename())
				.mode(GithubFileMode.valueOf(request.getMode()))
				.baseTreeSha(request.getBaseTreeSha())
				.commitMessage("giteditor commit")
				.build());
		return Response.success("파일을 성공적으로 커밋하였습니다.");
	}*/

	@PostMapping("/repo")
	public ResponseEntity<?> createRepo(@CurrentUser CustomUserDetails userDetails,
										@RequestBody GithubRequest.CreateRepo request) throws IOException {
		String created = githubService.createRepo(ServiceUserInfo.userId(userDetails.getUserEntityId()),
			CreateRepoCommand.builder()
				.repoName(request.getRepoName())
				.description(request.getDescription())
				.makePrivate(request.isMakePrivate())
				.build());
		return Response.success(GithubResponse.Repo.builder().repoName(created).build()
			, "리포지토리를 성공적으로 생성하였습니다.", HttpStatus.CREATED);
	}

	@DeleteMapping("/repo")
	public ResponseEntity<?> deleteRepo(@CurrentUser CustomUserDetails userDetails,
										@RequestParam String repoName) throws IOException {
		githubService.deleteRepo(ServiceUserInfo.userId(userDetails.getUserEntityId()),
			DeleteRepoCommand.builder()
				.repoName(repoName)
				.build());
		return Response.success("리포지토리를 성공적으로 삭제하였습니다.");
	}
}
