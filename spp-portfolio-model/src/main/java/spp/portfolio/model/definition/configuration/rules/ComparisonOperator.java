package spp.portfolio.model.definition.configuration.rules;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum ComparisonOperator
{
    GREATER_THAN(">"),
    GREATER_THAN_EQUAL(">="),
    LESS_THAN("<"),
    LESS_THAN_EQUAL("<="),
    NOT_EQUAL("!="),
    EQUAL("==");
    
    private final String symbol;
    
    public static ComparisonOperator getFromSymbol(String symbol)
    {
        switch (symbol)
        {
            case ">": return GREATER_THAN;
            case ">=": return GREATER_THAN_EQUAL;
            case "<": return LESS_THAN;
            case "<=": return LESS_THAN_EQUAL;
            case "!=": return NOT_EQUAL;
            case "==": return EQUAL;
            
            default:
                throw new IllegalArgumentException("Unexpected value: " + symbol);
        }
    }   
}
