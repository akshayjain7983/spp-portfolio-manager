package spp.portfolio.model.definition.configuration.rules;

import lombok.Data;

@Data
public class ComparisonExpression implements Expression
{
    private String leftSide;
    private ComparisonOperator operator;
    private String rightSide;
}
