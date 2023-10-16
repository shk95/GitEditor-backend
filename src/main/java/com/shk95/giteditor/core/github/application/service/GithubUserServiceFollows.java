package com.shk95.giteditor.core.github.application.service;

import com.shk95.giteditor.core.github.application.port.in.GetUserInfoUseCase;
import com.shk95.giteditor.core.github.application.port.out.GetCredentialPort;
import com.shk95.giteditor.core.github.application.port.out.GithubUserPort;
import com.shk95.giteditor.core.github.application.service.dto.PageInfo;
import com.shk95.giteditor.core.github.domain.GithubCredential;
import com.shk95.giteditor.core.github.domain.GithubRepo;
import com.shk95.giteditor.core.github.domain.GithubUser;
import com.shk95.giteditor.core.user.domain.user.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class GithubUserServiceFollows implements GetUserInfoUseCase {

	private final GetCredentialPort getCredentialPort;
	private final GithubUserPort githubUserPort;

	@Override
	public List<GithubUser> getUserFollowsSimpleList(UserId myId) {
		GithubCredential credential = getCredentialPort.fetch(myId.get());
		try {
			return githubUserPort.getUserFollowsInfo(credential);
		} catch (IOException e) { // github 인스턴스 구성, 자신의 레포지토리 조회 등에서 발생하는 예외(존재하지 않는 자원에 대한 요청).
			return new ArrayList<>(); // 팔로잉한 사용자가 없는것으로 간주. 비어있는 목록 반환.
		}
	}

	@Override
	public String getUserBio(UserId myId, String userLogin) {
		GithubCredential credential = getCredentialPort.fetch(myId.get());
		try {
			return githubUserPort.getBio(credential, userLogin);
		} catch (IOException e) {
			return "";
		}
	}

	@Override
	public List<GithubUser> getFollowersSimpleList(UserId myId) {
		GithubCredential credential = getCredentialPort.fetch(myId.get());
		try {
			return githubUserPort.getFollowersInfo(credential);
		} catch (IOException e) { // github 인스턴스 구성, 자신의 레포지토리 조회 등에서 발생하는 예외(존재하지 않는 자원에 대한 요청).
			return new ArrayList<>(); // 팔로워가 없는것으로 간주. 비어있는 목록 반환.
		}
	}

	@Override
	public List<GithubRepo> getAllStarredRepoSimpleList(UserId myId, PageInfo pageInfo) {
		GithubCredential credential = getCredentialPort.fetch(myId.get());
		try {
			return githubUserPort.getAllStarredRepo(credential, pageInfo);
		} catch (IOException e) { // github 인스턴스 구성, 자신의 레포지토리 조회 등에서 발생하는 예외(존재하지 않는 자원에 대한 요청).
			return new ArrayList<>(); // starred repository 가 없는것으로 간주한다.
		}
	}

	@Override
	public boolean isRepoStarred(UserId myId, String repoName) {
		return false;
	}
}
