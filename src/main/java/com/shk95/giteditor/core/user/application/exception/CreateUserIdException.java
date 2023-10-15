package com.shk95.giteditor.core.user.application.exception;

public class CreateUserIdException extends RuntimeException {

	public CreateUserIdException(String message) {
		super(message);
	}

	public CreateUserIdException(String message, Throwable cause) {
		super(message, cause);
	}
}
