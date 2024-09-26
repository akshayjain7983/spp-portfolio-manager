package spp.portfolio.constituents.rules.simple;

import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.securityOutpointMapKey;

import java.util.Optional;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class ExpressionFilter extends RelaxableFilter
{
    private Expression<?> expression;

    @Override
    protected Optional<Security> executeFilter(Optional<Security> security, ConcurrentApplicationContext context)
    {
        Class<?> expressionResultType = expression.resultType();
        Optional<Security> result = Optional.empty();
        if(Boolean.class.isAssignableFrom(expressionResultType))
        {
            result = executeBoolean(security, context);
        }
        
        if(result.isEmpty())
        {
            security.ifPresent(s->context.fetch(securityOutpointMapKey).put(s.getSecurityId(), expression.toString()));
        }
        
        return result;
    }

    @SuppressWarnings("unchecked")
    private Optional<Security> executeBoolean(Optional<Security> security, ConcurrentApplicationContext context)
    {
        Expression<Boolean> expressionBoolean = (Expression<Boolean>)expression;
        Boolean result = expressionBoolean.execute(security, context);
        return result ? security : Optional.empty();
    }
}
