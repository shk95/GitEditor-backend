package com.shk95.giteditor.old.common.file;

import com.amazonaws.services.s3.AmazonS3;
import com.shk95.giteditor.common.service.file.FileStorageResolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("prod")
@SpringBootTest
class FileStorageTest {

	@MockBean
	private AmazonS3 s3Client;

	@Autowired
	private FileStorageResolver s3FileStorage;

	@Test
	public void testSaveUploaded() throws IOException {
		// Given
		String folder = "testFolder1";
		String originalFileName = "test1.txt";
		MockMultipartFile file = new MockMultipartFile(
			"file",
			originalFileName,
			"text/plain",
			"test data".getBytes()
		);


		// When
		String result = s3FileStorage.resolve().saveUploaded(folder, file);

		// Then
		assertNotNull(result);

		System.out.println(result);
	}
}
