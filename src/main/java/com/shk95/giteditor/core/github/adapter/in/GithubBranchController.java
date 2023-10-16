package com.shk95.giteditor.core.github.adapter.in;

import com.shk95.giteditor.common.ServiceUserId;
import com.shk95.giteditor.common.security.CurrentUser;
import com.shk95.giteditor.common.security.UserAuthorize;
import com.shk95.giteditor.common.utils.Response;
import com.shk95.giteditor.core.auth.domain.CustomUserDetails;
import com.shk95.giteditor.core.github.application.port.in.GithubServiceUseCase;
import com.shk95.giteditor.core.github.application.service.command.Branch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RequestMapping("/git/branch")
@RequiredArgsConstructor
@UserAuthorize
@RestController
public class GithubBranchController {

	private final GithubServiceUseCase githubServiceUseCase;

	@PostMapping
	public ResponseEntity<?> createBranch(@CurrentUser CustomUserDetails userDetails,
	                                      @Validated @RequestBody GithubRequest.CreateBranch request) throws IOException {
		if (!userDetails.isGithubEnabled()) {
			return Response.fail("권한이 없습니다.", HttpStatus.FORBIDDEN);
		}
		githubServiceUseCase.createBranch(
			ServiceUserId.from(userDetails).get(),
			Branch.forCreate(
				request.getRepoName(),
				request.getBaseBranchName(),
				request.getNewBranchName()));
		return Response.success("브랜치를 성공적으로 생성하였습니다.");
	}

	@DeleteMapping
	public ResponseEntity<?> deleteBranch(@CurrentUser CustomUserDetails userDetails,
	                                      @Validated @RequestBody GithubRequest.DeleteBranch request) throws IOException {
		if (!userDetails.isGithubEnabled()) {
			return Response.fail("권한이 없습니다.", HttpStatus.FORBIDDEN);
		}
		githubServiceUseCase.deleteBranch(
			ServiceUserId.from(userDetails).get(),
			Branch.forDelete(
				request.getRepoName(),
				request.getBranchName()));
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
}
