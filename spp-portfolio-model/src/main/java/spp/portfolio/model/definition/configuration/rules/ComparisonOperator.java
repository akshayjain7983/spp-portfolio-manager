package spp.portfolio.model.definition.configuration.rules;

import lombok.Data;
import lombok.NoArgsConstructor;

public interface ComparisonOperator extends Operator
{
    static ComparisonOperator getComparisonOperator(String symbol)
    {
        switch (symbol)
        {
            case ">": return new GreaterThanOperator();
            case ">=": return new GreaterThanEqualOperator();
            case "<": return new LessThanOperator();
            case "<=": return new LessThanEqualOperator();
            case "!=": return new NotEqualOperator();
            case "==": return new EqualOperator();
            
            default:
                throw new IllegalArgumentException("Unexpected value: " + symbol);
        }
    }
    
    @Data @NoArgsConstructor public static class GreaterThanOperator implements ComparisonOperator{private final String symbol = ">";}
    @Data @NoArgsConstructor public static class GreaterThanEqualOperator implements ComparisonOperator{private final String symbol = ">=";}
    @Data @NoArgsConstructor public static class LessThanOperator implements ComparisonOperator{private final String symbol = "<";}
    @Data @NoArgsConstructor public static class LessThanEqualOperator implements ComparisonOperator{private final String symbol = "<=";}
    @Data @NoArgsConstructor public static class NotEqualOperator implements ComparisonOperator{private final String symbol = "!=";}
    @Data @NoArgsConstructor public static class EqualOperator implements ComparisonOperator{private final String symbol = "==";}    
}
