package com.shk95.giteditor.old.web.apis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void getUser() {
	}

	@Test
	public void testUpdateProfileImage() throws Exception {
		/*// Create a mock image file
		MockMultipartFile file = new MockMultipartFile(
			"file",
			"test.jpg",
			"image/jpeg",
			"test image content".getBytes());
		HttpHeaders headers = new HttpHeaders(){{
			add("Authorization","Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJHSVRIVUIsZ2l0aHViVGVzdCIsImF1dGgiOiJST0xFX1VTRVIiLCJ0eXBlIjoiYWNjZXNzIiwiaWF0IjoxNjg1MTg4MjQ3LCJleHAiOjE2ODUxOTAwNDd9.kgjfTWZG85KCoLdgrOJhQ8jPIFaSv8K-iYgfZ4nHKvk"	);
		}};

		// Perform the file upload request
		mockMvc.perform(MockMvcRequestBuilders.multipart("/user/profile/img")
				.file(file).headers(headers))
			.andExpect(status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("$.uploadedImageUrl").exists());*/
	}

	@Test
	void updatePassword() {
	}

	@Test
	void testUpdatePassword() {
	}

	@Test
	void verifyEmail() {
	}
}
