package org.lukosan.salix.thymeleaf;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.lukosan.salix.MapUtils;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

public class SalixProcessor extends StandardEvaluationContext {

	public String urlEncode(String href) {
		try {
			return URLEncoder.encode(href, "utf8");
		} catch (UnsupportedEncodingException e) {
			return href;
		}
	}
	
	public String json(Map<String, Object> map) {
		return null == map ? "" : MapUtils.asString(map);
	}
	
	public String safe(Map<String, Object> map, String expression) {
		return null == map ? "" : MapUtils.getString(map, expression);
	}

	public String safe(Map<String, Object> map, String expression, String defaultValue) {
		String value = null == map ? "" : MapUtils.getString(map, expression);
		return StringUtils.hasText(value) ? value : defaultValue;
	}

}