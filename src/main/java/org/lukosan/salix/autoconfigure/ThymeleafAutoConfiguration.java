package org.lukosan.salix.autoconfigure;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.PostConstruct;
import javax.servlet.Servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lukosan.salix.SalixPublisher;
import org.lukosan.salix.SalixScopeRegistry;
import org.lukosan.salix.thymeleaf.SalixDialect;
import org.lukosan.salix.thymeleaf.SalixTemplateResolver;
import org.lukosan.salix.thymeleaf.SalixView;
import org.lukosan.salix.thymeleaf.ThymeleafPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
@EnableConfigurationProperties(ThymeleafProperties.class)
@ConditionalOnProperty(prefix = "salix.thymeleaf", name = "enabled", matchIfMissing = true)
public class ThymeleafAutoConfiguration {

	@Configuration
	public static class SalixDefaultTemplateResolverConfiguration {
		
		private static final Log logger = LogFactory.getLog(SalixDefaultTemplateResolverConfiguration.class);
				
		@Autowired
		private ThymeleafProperties properties;
		@Autowired
		private SalixScopeRegistry registry;
		
		@Bean(name="salixTemplateResolver")
		public ITemplateResolver salixTemplateResolver() {
			SalixTemplateResolver resolver = new SalixTemplateResolver();
			resolver.setOrder(1);
			resolver.setPrefix("");
			resolver.setSuffix("");
			resolver.setTemplateMode(this.properties.getMode());
			resolver.setCharacterEncoding(this.properties.getEncoding().name());
			resolver.setCacheable(true);
			return resolver;
		}
		
		@Bean // will be provided by spring boot in future
		public ITemplateResolver defaultTemplateResolver() {
			SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
			resolver.setOrder(2);
			resolver.setPrefix(this.properties.getPrefix());
			resolver.setSuffix(this.properties.getSuffix());
			resolver.setTemplateMode(this.properties.getMode());
			resolver.setCharacterEncoding(this.properties.getEncoding().name());
			resolver.setCacheable(this.properties.isCache());
			return resolver;
		}
		
		@Bean
		public SalixDialect salixDialect() {
			return new SalixDialect();
		}
		
		@PostConstruct
		public void postConstruct() {
			if(logger.isInfoEnabled())
				logger.info("PostConstruct " + getClass().getSimpleName());
		}
		
	}
	
	@Configuration
	@ConditionalOnClass({ Servlet.class })
	@ConditionalOnWebApplication
	@ConditionalOnProperty(prefix = "salix.thymeleaf.frontend", name = "enabled", matchIfMissing = true)
	public static class SalixHandlerMappingConfiguration {

		@Bean
		public SalixPublisher thymeleafPublisher() {
			return new ThymeleafPublisher();
		}

	}
	
	@Configuration // will be provided by spring boot in future
	protected static class ThymeleafDefaultConfiguration {

		@Autowired
		private final Collection<ITemplateResolver> templateResolvers = Collections.emptySet();

		@Autowired(required = false)
		private final Collection<IDialect> dialects = Collections.emptySet();

		@Bean
		public SpringTemplateEngine templateEngine() {
			SpringTemplateEngine engine = new SpringTemplateEngine();
			for (ITemplateResolver templateResolver : this.templateResolvers) {
				engine.addTemplateResolver(templateResolver);
			}
			for (IDialect dialect : this.dialects) {
				engine.addDialect(dialect);
			}
			return engine;
		}

	}

	@Configuration
	@ConditionalOnClass({ Servlet.class })
	@ConditionalOnWebApplication // will be provided by spring boot in future
	protected static class ThymeleafViewResolverConfiguration {

		@Autowired
		private ThymeleafProperties properties;

		@Autowired
		private SpringTemplateEngine templateEngine;

		@Bean
		@ConditionalOnMissingBean(name = "thymeleafViewResolver")
		@ConditionalOnProperty(name = "spring.thymeleaf.enabled", matchIfMissing = true)
		public ThymeleafViewResolver thymeleafViewResolver() {
			ThymeleafViewResolver resolver = new ThymeleafViewResolver();
			resolver.setTemplateEngine(this.templateEngine);
			resolver.setCharacterEncoding(this.properties.getEncoding().name());
			resolver.setContentType(appendCharset(this.properties.getContentType().getType() + "/" + this.properties.getContentType().getSubtype(), resolver.getCharacterEncoding()));
			//resolver.setContentType(this.properties.getContentType().getType()); // XXX test this!
			resolver.setExcludedViewNames(this.properties.getExcludedViewNames());
			resolver.setViewNames(this.properties.getViewNames());
			resolver.setViewClass(SalixView.class);
			resolver.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
			return resolver;
		}
		
		private String appendCharset(String type, String charset) {
			if (type.contains("charset=")) {
				return type;
			}
			return type + ";charset=" + charset;
		}


	}

}