package com.shk95.giteditor.core.github.application.port.in;

import com.shk95.giteditor.core.github.application.service.dto.PageInfo;
import com.shk95.giteditor.core.github.domain.GithubRepo;
import com.shk95.giteditor.core.github.domain.GithubUser;
import com.shk95.giteditor.core.user.domain.user.UserId;
import org.springframework.lang.Nullable;

import java.util.List;

public interface GetUserInfoUseCase {

	String getUserBio(UserId myId, @Nullable String userLogin);// userLogin : 가져올 유저. 본인은 null

	List<GithubUser> getFollowersSimpleList(UserId myId);

	List<GithubUser> getUserFollowsSimpleList(UserId myId);

	List<GithubRepo> getAllStarredRepoSimpleList(UserId myId, @Nullable PageInfo pageInfo);

	boolean isRepoStarred(UserId myId, String repoName);

}
