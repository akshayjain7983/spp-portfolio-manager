package spp.portfolio.model.definition.configuration.rules;

import lombok.Data;

@Data
public class SimpleExpression implements Expression
{
    private String leftSide;
    private ComparisonOperator operator;
    private String rightSide;
}
