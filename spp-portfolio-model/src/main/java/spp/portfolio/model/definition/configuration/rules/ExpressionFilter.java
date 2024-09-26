package spp.portfolio.model.definition.configuration.rules;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExpressionFilter extends RelaxableFilter
{
    private Expression expression;
}
