package org.lukosan.salix.thymeleaf;

import java.util.Map;

import org.lukosan.salix.SalixProperties;
import org.lukosan.salix.SalixTemplate;
import org.lukosan.salix.mvc.SalixServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;

public class SalixTemplateResolver extends AbstractConfigurableTemplateResolver implements ITemplateResolver {
	
	@Autowired
	private SalixProperties salixProperties;
	
	@Autowired
	private SalixServiceProxy salixService;

	@Override
	protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate, String template,
			String resourceName, String characterEncoding, Map<String, Object> templateResolutionAttributes) {
		
		SalixTemplate salixTemplate = null;
		
		if(salixProperties.isMultisite() && templateResolutionAttributes.containsKey(SalixContext.SCOPE)) {
			String scope = templateResolutionAttributes.get(SalixContext.SCOPE).toString();
			salixTemplate = salixService.template(template, scope);
		} else
			salixTemplate = salixService.template(template);
			
		if(salixTemplate != null)
			return new SalixResourceTemplateResource(salixTemplate);
		
		return null;
	}

	
}
