package org.lukosan.salix.thymeleaf;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.lukosan.salix.SalixTemplate;
import org.thymeleaf.templateresource.ITemplateResource;

public class SalixResourceTemplateResource implements ITemplateResource {

	private SalixTemplate template;
	
	public SalixResourceTemplateResource(SalixTemplate template) {
		this.template = template;
	}
	
	@Override
	public String getDescription() {
		return String.format("%s in scope %s", template.getName(), template.getScope()); 
	}

	@Override
	public String getBaseName() {
		return template.getName();
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public Reader reader() throws IOException {
		final InputStream inputStream = IOUtils.toInputStream(template.getSource());
        return new BufferedReader(new InputStreamReader(new BufferedInputStream(inputStream)));
	}

	@Override
	public ITemplateResource relative(String relativeLocation) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
