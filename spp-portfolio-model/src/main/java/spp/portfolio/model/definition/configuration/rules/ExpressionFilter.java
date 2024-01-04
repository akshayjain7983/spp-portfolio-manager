package spp.portfolio.model.definition.configuration.rules;

import lombok.Data;

@Data
public class ExpressionFilter implements Filter
{
    private Expression expression;
}
