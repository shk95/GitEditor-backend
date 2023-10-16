package com.shk95.giteditor.core.github.application.port.out;

import com.shk95.giteditor.core.github.application.service.dto.PageInfo;
import com.shk95.giteditor.core.github.domain.GithubCredential;
import com.shk95.giteditor.core.github.domain.GithubRepo;
import com.shk95.giteditor.core.github.domain.GithubUser;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.util.List;

public interface GithubUserPort {

	String getBio(GithubCredential githubCredential, @Nullable String userLogin) throws IOException;

	List<GithubRepo> getAllStarredRepo(GithubCredential githubCredential, @Nullable PageInfo pageInfo) throws IOException;

	List<GithubUser> getFollowersInfo(GithubCredential githubCredential) throws IOException;

	List<GithubUser> getUserFollowsInfo(GithubCredential githubCredential) throws IOException;

	boolean isStarredRepo(GithubCredential githubCredential, String repoName) throws IOException;
}
