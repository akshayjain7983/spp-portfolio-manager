package spp.portfolio.model.definition.configuration.rules;

import lombok.Data;

@Data
public class ExistExpression implements Expression
{
    private Attribute<?> leftSide;
    private ExistOperator operator;
}
