package org.lukosan.salix.thymeleaf.expression;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.lukosan.salix.thymeleaf.SalixProcessor;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.expression.IExpressionObjectFactory;

public class SalixExpressionObjectFactory implements IExpressionObjectFactory {

	public static final String SALIX_EXPRESSION_OBJECT_NAME = "salix";
	
	protected static final Set<String> ALL_EXPRESSION_OBJECT_NAMES = 
			Collections.unmodifiableSet(new LinkedHashSet<String>(java.util.Arrays.asList(
	                new String[]{ SALIX_EXPRESSION_OBJECT_NAME }
	        )));
	
	public SalixExpressionObjectFactory() {
        super();
    }
	
	@Override
	public Set<String> getAllExpressionObjectNames() {
		return ALL_EXPRESSION_OBJECT_NAMES;
	}

	@Override
	public boolean isCacheable(final String expressionObjectName) {
        // All expression objects created by this factory are cacheable (template-scope)
        return true;
    }

	@Override
    public Object buildObject(final IExpressionContext context, final String expressionObjectName) {

        if (SALIX_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return new SalixProcessor();
        }

        return null;
    }
}
