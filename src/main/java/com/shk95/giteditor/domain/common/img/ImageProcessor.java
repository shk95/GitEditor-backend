package com.shk95.giteditor.domain.common.img;

import com.shk95.giteditor.utils.Size;

import java.io.IOException;

public interface ImageProcessor {

	void resize(String sourceFilePath, String targetFilePath, Size resizeTo) throws Exception;

	Size getSize(String imagePath) throws IOException;
}
