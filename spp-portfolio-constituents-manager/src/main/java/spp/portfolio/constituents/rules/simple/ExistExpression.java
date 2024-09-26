package spp.portfolio.constituents.rules.simple;

import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.parseAttribute;

import java.util.Objects;
import java.util.Optional;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import spp.portfolio.constituents.rules.Attribute;

public class ExistExpression implements Expression<Boolean>
{
    private Attribute<?> leftSide;
    private ExistOperator operator;
    
    @Override
    public Class<Boolean> resultType()
    {
	return Boolean.class;
    }

    @Override
    public Boolean execute(Optional<Security> security, ConcurrentApplicationContext context)
    {
	Object leftSideObject = parseAttribute.apply(leftSide, security);
	switch (operator) 
	{
        	case IS_NULL: return Objects.isNull(leftSideObject);
        	case NOT_NULL: return Objects.nonNull(leftSideObject);
        	default:
        	    throw new IllegalArgumentException("Unexpected value: " + operator);
	}
    }
    
    @Override
    public String toString()
    {
        return "ExistExpression [" + leftSide + " " + operator.getSymbol() + "]";
    }

}
