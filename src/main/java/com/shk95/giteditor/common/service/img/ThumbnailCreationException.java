package com.shk95.giteditor.common.service.img;

public class ThumbnailCreationException extends RuntimeException {
	private static final long serialVersionUID = 6259084841233699937L;

	public ThumbnailCreationException(String message) {
		super(message);
	}

	public ThumbnailCreationException(String message, Throwable cause) {
		super(message, cause);
	}
}
