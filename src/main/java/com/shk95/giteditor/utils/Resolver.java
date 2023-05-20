package com.shk95.giteditor.utils;

import org.springframework.validation.Errors;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Resolver {
	public static class error {
		public static LinkedList<LinkedHashMap<String, String>> inputFields(Errors errors) {
			LinkedList<LinkedHashMap<String, String>> errorList = new LinkedList<>();
			errors.getFieldErrors().forEach(e -> {
				LinkedHashMap<String, String> error = new LinkedHashMap<>();
				error.put("field", e.getField());
				error.put("message", e.getDefaultMessage());
				errorList.push(error);
			});
			return errorList;
		}
	}
}
