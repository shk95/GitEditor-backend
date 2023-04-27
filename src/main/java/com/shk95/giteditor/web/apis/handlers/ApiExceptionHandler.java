package com.shk95.giteditor.web.apis.handlers;

import com.shk95.giteditor.web.results.ApiResult;
import com.shk95.giteditor.web.results.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.UUID;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler({Exception.class})
	protected ResponseEntity<ApiResult> handle(RuntimeException ex) {
		String errorReferenceCode = UUID.randomUUID().toString();
		log.error("Unhandled exception error [code=" + errorReferenceCode + "]", ex);
		return Result.serverError("Sorry, there is an error on the server side.", errorReferenceCode);
	}

	@ExceptionHandler({MaxUploadSizeExceededException.class})
	protected ResponseEntity<ApiResult> handle(MaxUploadSizeExceededException ex) {
		return Result.failure("File exceed maximum size limit");
	}
}
