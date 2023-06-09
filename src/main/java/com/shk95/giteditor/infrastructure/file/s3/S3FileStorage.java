package com.shk95.giteditor.infrastructure.file.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.shk95.giteditor.config.ApplicationProperties;
import com.shk95.giteditor.domain.common.file.AbstractBaseFileStorage;
import com.shk95.giteditor.domain.common.file.FileStorageException;
import com.shk95.giteditor.domain.common.file.TempFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Component("s3FileStorage")
public class S3FileStorage extends AbstractBaseFileStorage {

	private final Environment environment;
	private final ApplicationProperties properties;
	private final String rootTempPath;
	private AmazonS3 s3;

	public S3FileStorage(Environment environment, ApplicationProperties properties) {
		this.environment = environment;
		this.properties = properties;
		this.rootTempPath = properties.getFileStorage().getTempFolder();

		if ("s3FileStorage".equals(this.properties.getFileStorage().getActive())) {
			this.s3 = initS3Client();
		}
	}

	public void createBucket(String bucketName) {
		if (s3.doesBucketExist(bucketName)) {
			log.info("Bucket name is not available."
				+ " Try again with a different Bucket name.");
			return;
		}
		s3.createBucket(bucketName);
	}

	public List<Bucket> getBuckets() {
		List<Bucket> buckets = s3.listBuckets();
		log.debug("Buckets : {}", buckets);
		return buckets;
	}

	public void deleteBucket(String bucketName) {
		try {
			s3.deleteBucket(bucketName);
		} catch (AmazonServiceException e) {
			log.error(e.getErrorMessage());
			e.printStackTrace();
		}
	}

	@Override
	public TempFile saveAsTempFile(String folder, MultipartFile multipartFile) {
		return saveMultipartFileToLocalTempFolder(rootTempPath, folder, multipartFile);
	}

	@Override
	public void saveTempFile(TempFile tempFile) {
		Assert.notNull(s3, "S3FileStorage must be initialized properly");

		String fileKey = tempFile.getFileRelativePath();
		String bucketName = properties.getFileStorage().getS3BucketName();
		Assert.hasText(bucketName, "Property `app.file-storage.s3-bucket-name` must not be blank");

		try {
			log.debug("Saving file `{}` to s3", tempFile.getFile().getName());
			PutObjectRequest putRequest = new PutObjectRequest(bucketName, fileKey, tempFile.getFile());
			putRequest.withCannedAcl(CannedAccessControlList.PublicRead);
			s3.putObject(putRequest);
			log.debug("File `{}` saved to s3. File key : `{}`", tempFile.getFile().getName(), fileKey);
		} catch (Exception e) {
			log.error("Failed to save file to s3", e);
			throw new FileStorageException("Failed to save file `" + tempFile.getFile().getName() + "` to s3", e);
		}
	}

	@Override
	public String saveUploaded(String folder, MultipartFile multipartFile) {// S3 upload
		Assert.notNull(s3, "S3FileStorage must be initialized properly");

		String originalFileName = multipartFile.getOriginalFilename();
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(multipartFile.getSize());
		metadata.setContentType(multipartFile.getContentType());
		metadata.addUserMetadata("Original-File-Name", originalFileName);
		String finalFileName = generateFileName(multipartFile);
		String s3ObjectKey = folder + "/" + finalFileName;

		String bucketName = properties.getFileStorage().getS3BucketName();
		Assert.hasText(bucketName, "Property `app.file-storage.s3-bucket-name` must not be blank");

		try {
			log.debug("Saving file `{}` to s3", originalFileName);
			PutObjectRequest putRequest = new PutObjectRequest(
				bucketName, s3ObjectKey, multipartFile.getInputStream(), metadata);
			putRequest.withCannedAcl(CannedAccessControlList.PublicRead);
			s3.putObject(putRequest);
			log.debug("File `{}` saved to s3 as `{}`", originalFileName, s3ObjectKey);
		} catch (Exception e) {
			log.error("Failed to save file to s3", e);
			throw new FileStorageException("Failed to save file `" + multipartFile.getOriginalFilename() + "` to s3", e);
		}
		log.info("Upload complete : {}", s3ObjectKey);
		return s3ObjectKey;
	}

	private AmazonS3 initS3Client() {
		String s3Region = properties.getFileStorage().getS3Region();
		Assert.hasText(s3Region, "Property `app.file-storage.s3-region` must not be blank");

//		if (environment.acceptsProfiles(Profiles.of("dev"))) {
		log.debug("Initializing dev S3 client with access key and secret key");

		String s3AccessKey = properties.getFileStorage().getS3AccessKey();
		String s3SecretKey = properties.getFileStorage().getS3SecretKey();
		assert s3AccessKey != null;
		assert s3SecretKey != null;

		BasicAWSCredentials awsCredentials = new BasicAWSCredentials(s3AccessKey, s3SecretKey);
		AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);

		AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard();
		builder.setRegion(s3Region);
		builder.withCredentials(credentialsProvider);
		return builder.build();
		/*} else {
			log.debug("Initializing default S3 client using IAM role");
			return AmazonS3ClientBuilder
				.standard()
				.withCredentials(new InstanceProfileCredentialsProvider(false))
				.withRegion(s3Region).build();
		}*/
	}
}
