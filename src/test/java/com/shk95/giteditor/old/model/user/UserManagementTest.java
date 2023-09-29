package com.shk95.giteditor.old.model.user;

import com.shk95.giteditor.core.user.domain.user.User;
import com.shk95.giteditor.core.user.domain.user.UserId;
import com.shk95.giteditor.common.constant.ProviderType;
import com.shk95.giteditor.common.security.Role;
import com.shk95.giteditor.core.user.domain.provider.Provider;
import com.shk95.giteditor.core.user.domain.provider.ProviderId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("User Actions")
@DataJpaTest
class UserManagementTest {

	@Autowired
	private TestEntityManager entityManager;

	private User user;
	private Provider provider;


	@BeforeEach
	public void setUp() {
		UserId userId = new UserId(ProviderType.GITHUB, "testUser");
		ProviderId providerId = new ProviderId(ProviderType.GITHUB, "testProvider");

		user = User.builder()
			.userId(userId)
			.role(Role.USER)
			.build();

		provider = Provider.builder()
			.providerId(providerId)
			.providerEmail("test@test.com")
			.build();

		user.addProvider(provider);

		entityManager.persist(user);
	}

	@AfterEach
	public void cleanUp() {
		entityManager.clear();
	}

	@Test
	void updatePassword() {
	}

	@Test
	void verifyEmail() {
	}

	@Test
	void changeEmail() {
	}

	@Test
	void uploadProfileImage() {
	}

	@Test
	void testDeleteUserAlsoDeletesProvider() {
		entityManager.remove(user);
		entityManager.flush();

		User foundUser = entityManager.find(User.class, user.getUserId());
		Provider foundProvider = entityManager.find(Provider.class, provider.getProviderId());

		assertNull(foundUser);
		assertNull(foundProvider);
	}

	@Test
	void updateUser() {
	}

	@Test
	void updateOpenAIService() {
	}

	@Test
	void addGithubAccount() {
	}
}
