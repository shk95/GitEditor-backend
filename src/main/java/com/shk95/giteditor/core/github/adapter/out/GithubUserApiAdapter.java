package com.shk95.giteditor.core.github.adapter.out;

import com.shk95.giteditor.core.github.application.port.out.GithubUserPort;
import com.shk95.giteditor.core.github.application.service.dto.PageInfo;
import com.shk95.giteditor.core.github.domain.GithubCredential;
import com.shk95.giteditor.core.github.domain.GithubRepo;
import com.shk95.giteditor.core.github.domain.GithubUser;
import com.shk95.giteditor.core.github.infrastructure.GithubInitializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.shk95.giteditor.core.github.infrastructure.GithubUtils.githubRepoMapper;
import static com.shk95.giteditor.core.github.infrastructure.GithubUtils.githubUserMapper;

@Slf4j
@RequiredArgsConstructor
@Component
public class GithubUserApiAdapter implements GithubUserPort {

	private final GithubInitializer githubInitializer;

	@Override
	public String getBio(GithubCredential githubCredential, String userLogin) throws IOException {
		GitHub gitHub = githubInitializer.getInstance(githubCredential);
		return userLogin == null
			? gitHub.getMyself().getBio()
			: gitHub.getUser(userLogin).getBio();
	}

	@Override
	public List<GithubRepo> getAllStarredRepo(GithubCredential githubCredential, @Nullable PageInfo pageInfo) throws IOException {
		GitHub gitHub = githubInitializer.getInstance(githubCredential);
		GHMyself myself = gitHub.getMyself();
		PagedIterable<GHRepository> starredRepos = myself.listStarredRepositories();

		if (pageInfo == null) {
			return starredRepos
				.toList().stream()
				.map(githubRepoMapper)
				.collect(Collectors.toList());
		}

		int i = pageInfo.getPageAt();
		PagedIterator<GHRepository> iterator = starredRepos.withPageSize(pageInfo.getPageSize()).iterator();
		List<GHRepository> starredReposPage = new ArrayList<>();
		while (iterator.hasNext()) {
			starredReposPage = iterator.nextPage();
			if (i-- == 1) {
				break;
			}
		}
		return starredReposPage.stream()
			.map(githubRepoMapper)
			.collect(Collectors.toList());
	}

	@Override
	public List<GithubUser> getFollowersInfo(GithubCredential githubCredential) throws IOException {
		GitHub gitHub = githubInitializer.getInstance(githubCredential);
		return gitHub.getMyself()
			.getFollowers().stream()
			.map(githubUserMapper)
			.collect(Collectors.toList());
	}

	@Override
	public List<GithubUser> getUserFollowsInfo(GithubCredential githubCredential) throws IOException {
		GitHub gitHub = githubInitializer.getInstance(githubCredential);
		return gitHub.getMyself()
			.getFollows().stream()
			.map(githubUserMapper)
			.collect(Collectors.toList());
	}

	@Override
	public boolean isStarredRepo(GithubCredential githubCredential, String repoName) throws IOException {
		GitHub gitHub = githubInitializer.getInstance(githubCredential);
		return false;
	}
}
