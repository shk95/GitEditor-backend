package com.shk95.giteditor.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Writer;

@NoArgsConstructor
@Slf4j
public class JsonUtils {
	public static String toJson(Object object) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Failed to convert object to JSON string", e);
		}
	}

	public static <T> T toObject(String json, Class<T> clazz) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readValue(json, clazz);
		} catch (IOException e) {
			log.error("Failed to convert string `" + json + "` class `" + clazz.getName() + "`", e);
			return null;
		}
	}

	public static void write(Writer writer, Object value) throws IOException {
		new ObjectMapper().writeValue(writer, value);
	}
}
