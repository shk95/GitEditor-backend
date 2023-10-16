package com.shk95.giteditor.core.github.infrastructure;

import com.shk95.giteditor.core.github.domain.GithubOwner;
import com.shk95.giteditor.core.github.domain.GithubRepo;
import com.shk95.giteditor.core.github.domain.GithubUser;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHBlob;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.function.Function;

@Slf4j
public class GithubUtils {

	public static Function<GHUser, GithubUser> githubUserMapper =
		ghUser -> GithubUser.builder()
			.login(ghUser.getLogin())
			.avatar_url(ghUser.getAvatarUrl())

			.followers(safeGetter(ghUser::getFollowersCount))
			.following(safeGetter(ghUser::getFollowingCount))
			.public_repos(safeGetter(ghUser::getPublicRepoCount))
			.public_gists(safeGetter(ghUser::getPublicGistCount))
			.location(safeGetter(ghUser::getLocation))
			.blog(safeGetter(ghUser::getBlog))
			.email(safeGetter(ghUser::getEmail))
			.bio(safeGetter(ghUser::getBio))
			.name(safeGetter(ghUser::getName))
			.company(safeGetter(ghUser::getCompany))
			.type(safeGetter(ghUser::getType))
			.twitter_username(safeGetter(ghUser::getTwitterUsername)).build();

	public static Function<GHRepository, GithubRepo> githubRepoMapper = ghRepository -> {
/*				String readMe;
				try { // readme
					readMe = convertStreamToString(ghRepository.getReadme().read());
				} catch (IOException ignored) { // null 무시
					readMe = null;
				}*/
		GithubOwner owner;
		try { // owner
			GHUser ownerInfo = ghRepository.getOwner();
			owner = GithubOwner.builder()
				.avatarUrl(ownerInfo.getAvatarUrl())
				.email(ownerInfo.getEmail())
				.name(ownerInfo.getName())
				.loginId(ownerInfo.getLogin())
				.type(ownerInfo.getType()).build();
		} catch (IOException ignored) { // null 무시
			owner = null;
		}

		return GithubRepo.builder()
			.repoName(ghRepository.getFullName())
			.url(ghRepository.getUrl().toString())
//					.readme(readMe)
			.owner(owner)
			.build();
	};

	public static String readBlobAsString(GHBlob content) {
		StringBuilder sb = new StringBuilder();
		try {
			InputStream is = content.read();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
		} catch (Exception e) {
			log.warn("Unexpected exception occurred while reading blob as string. {}", e.getMessage());
			return null;
		}
		return sb.toString();
	}

	public static <T> T safeGetter(Callable<T> getter) {
		T t;
		try {
			t = getter.call();
		} catch (Exception e) {
			t = null;
		}
		return t;
	}

	public static int safeIntGetter(Callable<Integer> getter) {
		try {
			return getter.call();
		} catch (Exception e) {
			return -1;
		}
	}
}
