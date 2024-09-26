package spp.portfolio.constituents.rules.simple;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum ExistOperator
{
    IS_NULL("Is Null"), 
    NOT_NULL("Is Not Null");
    
    private final String symbol;
    
    public static ExistOperator getFromSymbol(String symbol)
    {
        switch (symbol)
        {
            case "Is Null": return IS_NULL;
            case "Is Not Null": return NOT_NULL;
            
            default:
                throw new IllegalArgumentException("Unexpected value: " + symbol);
        }
    }   
}
