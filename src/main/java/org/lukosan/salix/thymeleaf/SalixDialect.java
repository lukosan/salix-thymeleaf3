package org.lukosan.salix.thymeleaf;

import org.lukosan.salix.thymeleaf.expression.SalixExpressionObjectFactory;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

public class SalixDialect extends AbstractDialect implements IExpressionObjectDialect {

	public static final String NAME = "Salix";
    public static final String DEFAULT_PREFIX = "salix";
    public static final int PROCESSOR_PRECEDENCE = 500;
    
	public static final String EXPRESSION_OBJECT_NAME = "salix";

	public static final IExpressionObjectFactory EXPRESSION_OBJECT_FACTORY = new SalixExpressionObjectFactory();
    
	
	public SalixDialect() {
        super(NAME);
    }
    
    public String getPrefix() {
        return DEFAULT_PREFIX;
    }

    public int getDialectProcessorPrecedence() {
        return PROCESSOR_PRECEDENCE;
    }

	@Override
	public IExpressionObjectFactory getExpressionObjectFactory() {
		return EXPRESSION_OBJECT_FACTORY;
	}

}
