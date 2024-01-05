package spp.portfolio.model.definition.configuration.rules;

import lombok.Data;
import lombok.NoArgsConstructor;

public interface BooleanOperator extends Operator
{
    static BooleanOperator getBooleanOperator(String symbol)
    {
        switch (symbol)
        {
            case "AND": return new AndOperator();
            case "OR": return new OrOperator();
            case "XOR": return new XorOperator();
            case "NOT": return new NotOperator();
            
            default:
                throw new IllegalArgumentException("Unexpected value: " + symbol);
        }
    }
    
    @Data @NoArgsConstructor public static class AndOperator implements BooleanOperator{private final String symbol = "AND";}
    @Data @NoArgsConstructor public static class OrOperator implements BooleanOperator{private final String symbol = "OR";}
    @Data @NoArgsConstructor public static class XorOperator implements BooleanOperator{private final String symbol = "XOR";}
    @Data @NoArgsConstructor public static class NotOperator implements BooleanOperator{private final String symbol = "NOT";}
}
