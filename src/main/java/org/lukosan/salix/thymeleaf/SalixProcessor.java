package org.lukosan.salix.thymeleaf;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SalixProcessor extends StandardEvaluationContext {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	static {
		MAPPER.findAndRegisterModules();
	}
	
	public String urlEncode(String href) {
		try {
			return URLEncoder.encode(href, "utf8");
		} catch (UnsupportedEncodingException e) {
			return href;
		}
	}
	
	public String json(Map<String, Object> map) {
		try {
			return MAPPER.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			return map.toString();
		}
	}
}
