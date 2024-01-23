package spp.portfolio.model.definition.configuration.rules;

import lombok.Data;

@Data
public class ComparisonExpression implements Expression
{
    private Attribute<?> leftSide;
    private ComparisonOperator operator;
    private Attribute<?> rightSide;
}
