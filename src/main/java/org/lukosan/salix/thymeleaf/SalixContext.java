package org.lukosan.salix.thymeleaf;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.thymeleaf.context.IContext;

public class SalixContext implements IContext {

	public static final String SCOPE = "scope";

	private static final String[] vars = { SCOPE };

	
	private String scope;

	public SalixContext(String scope) {
		this.scope = scope;
	}
		
	public String getScope() {
		return scope;
	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean containsVariable(String name) {
		return getVariableNames().contains(name);
	}

	@Override
	public Set<String> getVariableNames() {
		return Arrays.stream(vars).collect(Collectors.toSet());
	}

	@Override
	public Object getVariable(String name) {
		if(name.equalsIgnoreCase("scope")) {
			return getScope();
		}
		return null;
	}
}