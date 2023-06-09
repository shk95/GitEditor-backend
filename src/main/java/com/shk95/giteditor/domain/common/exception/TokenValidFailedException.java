package com.shk95.giteditor.domain.common.exception;

public class TokenValidFailedException extends RuntimeException {

	public TokenValidFailedException() {
		super("Failed to generate Token. ");
	}

	public TokenValidFailedException(String message) {
		super(message);
	}
}
