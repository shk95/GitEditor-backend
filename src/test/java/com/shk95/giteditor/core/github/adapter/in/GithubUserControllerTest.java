package com.shk95.giteditor.core.github.adapter.in;

import com.shk95.giteditor.core.github.domain.GithubCredential;
import com.shk95.giteditor.core.github.infrastructure.GithubInitializer;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@SpringBootTest
@ActiveProfiles(profiles = {"dev", "test"})
class GithubUserControllerTest {

	@Value("${test.github.login-id}")
	String loginId;
	@Value("${test.github.api-key}")
	String apiKey;

	@Autowired
	GithubInitializer githubInitializer;


	public static void main(String[] args) {


	}

	private static String convertStreamToString(InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();
		String line;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
			while ((line = br.readLine()) != null) {
				sb.append(line).append("\n");
			}
		}
		return sb.toString();
	}

	@Test
	void getAllFollowingUserInfo() {
	}

	@Test
	void getAllFollowerUserInfo() {
	}

	@Test
	void getUserReadme() {
	}

	@Test
	void getMyStarsList() {
	}

	@Test
	void getStarsListAndGetReadmeContent() throws IOException {
		GithubCredential githubCredential = new GithubCredential(apiKey, loginId);
		GitHub github = githubInitializer.getInstance(githubCredential);

		String t = github.getMyself().getLogin();
		System.out.println("=========================================");
		System.out.println("Login : [" + t + "]");
		GHMyself myself = github.getMyself();
		PagedIterable<GHRepository> starredRepos = myself.listStarredRepositories();

		int i = 0;

		for (GHRepository repo : starredRepos) {
			System.out.println(repo.getFullName());

			GHContent readme;
			try {
				readme = repo.getFileContent("README.md");
			} catch (GHFileNotFoundException e) {
				System.out.println("Readme 없음");
				System.out.println(e.getMessage());
				continue;
			}
			System.out.println("Repository: " + repo.getFullName());
			System.out.println("README Content: ");
			System.out.println(convertStreamToString(readme.read()));
			System.out.println("--------------------------------------------------");
			if (i++ > 3) {
				break;
			}
		}
	}

}
