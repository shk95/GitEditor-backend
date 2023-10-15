package com.shk95.giteditor.common.service.img;

import com.shk95.giteditor.common.service.file.FileStorage;
import com.shk95.giteditor.common.service.file.TempFile;
import com.shk95.giteditor.common.utils.ImageUtils;
import com.shk95.giteditor.common.utils.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.shk95.giteditor.config.Constants.Thumbnail.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class ThumbnailCreator {


	private final ImageProcessor imageProcessor;

	/**
	 * Create a thumbnail file and save to the storage
	 *
	 * @param fileStorage   file storage
	 * @param tempImageFile a temp image file
	 */
	public void create(FileStorage fileStorage, TempFile tempImageFile) {
		Assert.isTrue(tempImageFile.getFile().exists(), "Image file `" +
			tempImageFile.getFile().getAbsolutePath() + "` must exist");

		String ext = FilenameUtils.getExtension(tempImageFile.getFile().getName());
		if (!SUPPORTED_EXTENSIONS.contains(ext)) {
			throw new ThumbnailCreationException("Not supported image format for creating thumbnail");
		}

		log.debug("Creating thumbnail for file `{}` ext `{}`", tempImageFile.getFile().getName(), ext);

		try {
			String sourceFilePath = tempImageFile.getFile().getAbsolutePath();
			if (!sourceFilePath.endsWith("." + ext)) {
				throw new IllegalArgumentException("Image file's ext doesn't match the one in file descriptor");
			}
			String tempThumbnailFilePath = ImageUtils.getThumbnailVersion(tempImageFile.getFile().getAbsolutePath());
			Size resizeTo = getTargetSize(sourceFilePath);
			imageProcessor.resize(sourceFilePath, tempThumbnailFilePath, resizeTo);

			Path fileAbsolutePath = Paths.get(tempThumbnailFilePath);
			fileStorage.saveTempFile(TempFile.create(tempImageFile.tempRootPath(), fileAbsolutePath));
			// Delete temp thumbnail file
			Files.delete(fileAbsolutePath);
		} catch (Exception e) {
			log.error("Failed to create thumbnail for file `" + tempImageFile.getFile().getAbsolutePath() + "`", e);
			throw new ThumbnailCreationException("Creating thumbnail failed", e);
		}
	}

	private Size getTargetSize(String imageFilePath) throws IOException {
		Size actualSize = imageProcessor.getSize(imageFilePath);
		if (actualSize.getWidth() <= MAX_WIDTH && actualSize.getHeight() <= MAX_HEIGHT) {
			return actualSize;
		}

		if (actualSize.getWidth() > actualSize.getHeight()) {
			int width = MAX_WIDTH;
			int height = (int) Math.floor(((double) width / (double) actualSize.getWidth()) * actualSize.getHeight());
			return new Size(width, height);
		} else {
			int height = MAX_HEIGHT;
			int width = (int) Math.floor(((double) height / (double) actualSize.getHeight()) * actualSize.getWidth());
			return new Size(width, height);
		}
	}
}
