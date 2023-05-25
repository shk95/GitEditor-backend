package com.shk95.giteditor.domain.common.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
		// Verify s3Client.putObject() was called
		verify(s3Client, times(1)).putObject(any(PutObjectRequest.class));
	}
}
