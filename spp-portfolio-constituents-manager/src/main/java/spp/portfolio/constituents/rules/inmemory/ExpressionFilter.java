package spp.portfolio.constituents.rules.inmemory;

import java.util.Optional;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import lombok.Data;
import spp.portfolio.constituents.rules.Security;

@Data
public class ExpressionFilter implements Filter
{
    private Expression<?> expression;

    @Override
    public Optional<Security> execute(Optional<Security> security, ConcurrentApplicationContext context)
    {
        Class<?> expressionResultType = expression.resultType();
        if(Boolean.class.isAssignableFrom(expressionResultType))
        {
            return executeBoolean(security, context);
        }
        
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private Optional<Security> executeBoolean(Optional<Security> security, ConcurrentApplicationContext context)
    {
        Expression<Boolean> expressionBoolean = (Expression<Boolean>)expression;
        Boolean result = expressionBoolean.execute(security, context);
        return result ? security : Optional.empty();
    }
}
