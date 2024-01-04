package spp.portfolio.model.definition.configuration.rules;

import lombok.Data;
import lombok.NoArgsConstructor;

public interface BooleanOperator extends Operator
{
    static BooleanOperator getBooleanOperator(String symbol)
    {
        switch (symbol)
        {
            case "&&": return new AndOperator();
            case "||": return new OrOperator();
            case "^": return new XorOperator();
            case "!": return new NotOperator();
            
            default:
                throw new IllegalArgumentException("Unexpected value: " + symbol);
        }
    }
    
    @Data @NoArgsConstructor public static class AndOperator implements BooleanOperator{private final String symbol = "&&";}
    @Data @NoArgsConstructor public static class OrOperator implements BooleanOperator{private final String symbol = "||";}
    @Data @NoArgsConstructor public static class XorOperator implements BooleanOperator{private final String symbol = "^";}
    @Data @NoArgsConstructor public static class NotOperator implements BooleanOperator{private final String symbol = "!";}
}
