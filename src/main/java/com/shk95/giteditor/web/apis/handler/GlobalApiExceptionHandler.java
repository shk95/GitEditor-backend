package com.shk95.giteditor.web.apis.handler;

import com.shk95.giteditor.domain.model.github.GithubInitException;
import com.shk95.giteditor.utils.Response;
import io.github.aminovmaksim.chatgpt4j.ChatGPTClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalApiExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler({Exception.class})
	protected ResponseEntity<?> handle(RuntimeException ex, HttpServletResponse httpServletResponse) {

		log.error("Unhandled Runtime exception occurred. cause : [" + ex.getCause() + "]" + "\nError message : [" + ex.getMessage() + "]");
		return Response.fail(ex.getMessage(), "Sorry, there was an error on the server side.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({IOException.class})
	protected ResponseEntity<?> handle(IOException ex, HttpServletResponse httpServletResponse) {

		log.error("IOException occurred. cause : [" + ex.getCause() + "]" + "\nError message : [" + ex.getMessage() + "]");
		return Response.fail(ex.getMessage(), "Sorry, there was an error on the server side.", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({MaxUploadSizeExceededException.class})
	protected ResponseEntity<?> handle(MaxUploadSizeExceededException ex) {
		log.info("Uploaded File exceed maximum size limit");
		return Response.fail(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({AccessDeniedException.class})
	protected ResponseEntity<?> handle(AccessDeniedException ex) {
		log.info("Request denied. Illegal access token submitted");
		return Response.fail(ex.getMessage(), HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler({IllegalArgumentException.class})
	protected ResponseEntity<?> handle(IllegalArgumentException ex) {
		log.warn("Illegal Argument Exception : {}", ex.getMessage());
		return Response.fail(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({ChatGPTClientException.class})
	protected ResponseEntity<?> handle(ChatGPTClientException ex) {
		log.warn("ChatGPT Exception : {}", ex.getMessage());
		return Response.fail(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({GithubInitException.class})
	protected ResponseEntity<?> handle(GithubInitException ex) {
		log.warn("Github Init Exception : {}", ex.getMessage());
		return Response.fail("깃허브 서비스 인증 실패.", HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
