package com.shk95.giteditor.common.utils;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Response {

	public static ResponseEntity<?> success(Object data, String msg, HttpStatus status) {
		Body body = Body.builder()
			.status(status.value())
			.data(data)
			.result("success")
			.message(msg)
			.error(Collections.emptyList())
			.build();
		return ResponseEntity.ok(body);
	}

	/**
	 * <p> 메세지만 가진 성공 응답을 반환한다.</p>
	 * <pre>
	 *     {
	 *         "status" : 200,
	 *         "result" : success,
	 *         "message.http" : message.http,
	 *         "data" : [],
	 *         "error" : []
	 *     }
	 * </pre>
	 *
	 * @param msg 응답 바디 message.http 필드에 포함될 정보
	 * @return 응답 객체
	 */
	public static ResponseEntity<?> success(String msg) {
		return success(Collections.emptyList(), msg, HttpStatus.OK);
	}

	/**
	 * <p> 데이터만 가진 성공 응답을 반환한다.</p>
	 * <pre>
	 *     {
	 *         "status" : 200,
	 *         "result" : success,
	 *         "message.http" : null,
	 *         "data" : [{data1}, {data2}...],
	 *         "error" : []
	 *     }
	 * </pre>
	 *
	 * @param data 응답 바디 data 필드에 포함될 정보
	 * @return 응답 객체
	 */
	public static ResponseEntity<?> success(Object data) {
		return success(data, null, HttpStatus.OK);
	}

	/**
	 * <p> 성공 응답만 반환한다. </p>
	 * <pre>
	 *     {
	 *         "status" : 200,
	 *         "result" : success,
	 *         "message.http" : null,
	 *         "data" : [],
	 *         "error" : []
	 *     }
	 * </pre>
	 *
	 * @return 응답 객체
	 */
	public static ResponseEntity<?> success() {
		return success(Collections.emptyList(), null, HttpStatus.OK);
	}

	public static ResponseEntity<?> fail(Object data, String msg, HttpStatus status) {
		Body body = Body.builder()
			.status(status.value())
			.data(data)
			.result("fail")
			.message(msg)
			.error(Collections.emptyList())
			.build();
		return ResponseEntity.ok(body);
	}

	/**
	 * <p> 메세지를 가진 실패 응답을 반환한다. </p>
	 * <pre>
	 *     {
	 *         "status" : HttpStatus Code,
	 *         "result" : fail,
	 *         "message.http" : message.http,
	 *         "data" : [],
	 *         "error" : [{error1}, {error2}...]
	 *     }
	 * </pre>
	 *
	 * @param msg    응답 바디 message.http 필드에 포함될 정보
	 * @param status 응답 바디 status 필드에 포함될 응답 상태 코드
	 * @return 응답 객체
	 */
	public static ResponseEntity<?> fail(String msg, HttpStatus status) {
		return fail(Collections.emptyList(), msg, status);
	}

	public static ResponseEntity<?> invalidFields(LinkedList<LinkedHashMap<String, String>> errors) {
		Body body = Body.builder()
			.status(HttpStatus.BAD_REQUEST.value())
			.data(Collections.emptyList())
			.result("fail")
			.message("")
			.error(errors)
			.build();
		return ResponseEntity.ok(body);
	}

	@Getter
	@Builder
	private static class Body {

		private final int status;
		private final String result;
		private final String message;
		private final Object data;
		private final Object error;
	}
}
