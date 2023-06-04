package com.shk95.giteditor.domain.common.img.impl;

import com.shk95.giteditor.domain.common.img.ImageProcessor;
import com.shk95.giteditor.utils.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

@Slf4j
@Primary
@Component
public class ImageIOProcessorImpl implements ImageProcessor {

	@Override
	public void resize(String sourceFilePath, String targetFilePath, Size resizeTo) throws IOException {
		log.info("Invoked [{}.resize]", getClass());
		// reads input image
		File inputFile = new File(sourceFilePath);
		BufferedImage inputImage = ImageIO.read(inputFile);

		// creates output image
		BufferedImage outputImage = new BufferedImage(resizeTo.getWidth(), resizeTo.getHeight(), inputImage.getType());

		// scales the input image to the output image
		Graphics2D g2d = outputImage.createGraphics();
		g2d.drawImage(inputImage, 0, 0, resizeTo.getWidth(), resizeTo.getHeight(), null);
		g2d.dispose();

		// extracts extension of output file
		String formatName = targetFilePath.substring(targetFilePath.lastIndexOf(".") + 1);

		// if the format is JPEG, sets the quality of the image
		if ("jpg".equalsIgnoreCase(formatName) || "jpeg".equalsIgnoreCase(formatName)) {
			Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
			if (!writers.hasNext()) {
				throw new IllegalStateException("No writers found");
			}
			ImageWriter writer = writers.next();
			ImageOutputStream ios = ImageIO.createImageOutputStream(new File(targetFilePath));
			writer.setOutput(ios);
			ImageWriteParam param = new JPEGImageWriteParam(null);
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(0.7f); // Change this value if needed. The higher value, the higher quality
			writer.write(null, new IIOImage(outputImage, null, null), param);
			ios.close();
			writer.dispose();
		} else {
			ImageIO.write(outputImage, formatName, new File(targetFilePath));
		}
	}

	@Override
	public Size getSize(String imagePath) throws IOException {
		log.info("Invoked [{}.getSize]", getClass());
		File inputFile = new File(imagePath);
		BufferedImage inputImage = ImageIO.read(inputFile);
		return new Size(inputImage.getWidth(), inputImage.getHeight());
	}
}
