package com.shk95.giteditor.domain.application.impl;

import com.shk95.giteditor.domain.common.constant.ProviderType;
import com.shk95.giteditor.domain.common.security.Role;
import com.shk95.giteditor.domain.model.user.User;
import com.shk95.giteditor.domain.model.user.UserRepository;
import com.shk95.giteditor.web.apis.request.AuthRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.yml")
class UserServiceImplTest {

	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Mock
	private UserRepository userRepository;

	static AuthRequest.Signup.Default createSignupRequestDto() {
		AuthRequest.Signup.Default signUp = new AuthRequest.Signup.Default();
		signUp.setUserId("test1234");
		signUp.setPassword("QWEqwe123!");
		signUp.setDefaultEmail("test@test.com");
		return signUp;
	}

	static AuthRequest.Login createLoginRequestDto() {
		AuthRequest.Login login = new AuthRequest.Login();
		login.setUserId("test1234");
		login.setPassword("QWEqwe123!");
		return login;
	}

	@BeforeEach
	void setUp() {

	}

	@Test
	void loadUserByUsername() {
	}

	@Test
	void signUp_willSuccess() {
		System.out.println("#####" + userService.signupDefault(createSignupRequestDto()));

	}

	@Test
	void login() {
	}

	@Test
	void reissue() {
	}

	@Test
	void logout() {
	}

	@Test
	void authority() {
	}

	@Test
	void testLoadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
		User user = User.builder()
			.userId("testuser")
			.password("testpassword")
			.role(Role.USER)
			.build();
		when(userRepository.findByUserIdAndProviderType("testuser", ProviderType.LOCAL)).thenReturn(Optional.of(user));
		assertEquals(user.getUserId(), userService.loadUserByUsername("testuser").getUsername());
		assertEquals(user.getPassword(), userService.loadUserByUsername("testuser").getPassword());
		assertEquals(user.getRole(), userService.loadUserByUsername("testuser").getAuthorities());
	}

	@Test
	void testLoadUserByUsername_WhenUserDoesNotExist_ShouldThrowUsernameNotFoundException() {
		when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());
		assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("nonexistentuser"));
	}
/*

	@Test
	void testSignUp_WhenUserDoesNotExist_ShouldCreateUserAndSendWelcomeMessage() {
		when(userRepository.existsByUsername("testuser")).thenReturn(false);
		when(userRepository.existsByEmailAddress("testemail@example.com")).thenReturn(false);
		when(passwordEncoder.encode("testpassword")).thenReturn("encodedpassword");
		ResponseEntity<?> response = userService.signUp(signUp);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("회원가입에 성공했습니다.", response.getBody());
		verify(userRepository, times(1)).save(any(User.class));
		verify(mailManager, times(1)).send(any(String.class), any(String.class), any(MessageVariable.class));
	}

	@Test
	void testSignUp_WhenUserExistsByUsername_ShouldReturnBadRequestResponse() {
		UserRequestDto.SignUp signUp = new UserRequestDto.SignUp("testuser", "testpassword", "testemail@example.com");
		when(userRepository.existsByUsername("testuser")).thenReturn(true);
		ResponseEntity<?> response = userService.signUp(signUp);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("이미 회원가입된 아이디 입니다.", response.getBody());
		verify(userRepository, never()).save(any(User.class));

	}
*/

}
