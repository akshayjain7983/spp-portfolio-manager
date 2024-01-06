package spp.portfolio.constituents.rules.inmemory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum BooleanOperator
{
    AND("AND"),
    OR("OR"),
    XOR("XOR"),
    NOT("NOT");
    
    private final String symbol;
    
    public static BooleanOperator getFromSymbol(String symbol)
    {
        switch (symbol)
        {
            case "AND": return AND;
            case "OR": return OR;
            case "XOR": return XOR;
            case "NOT": return NOT;
            
            default:
                throw new IllegalArgumentException("Unexpected value: " + symbol);
        }
    }
}
