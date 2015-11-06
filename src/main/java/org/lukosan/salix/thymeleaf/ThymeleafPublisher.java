package org.lukosan.salix.thymeleaf;

import org.lukosan.salix.SalixPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.spring4.SpringTemplateEngine;

public class ThymeleafPublisher implements SalixPublisher {

	@Autowired
	private SpringTemplateEngine templateEngine;

	@Override
	public void publish() {
		templateEngine.getCacheManager().clearAllCaches();
	}
	
}