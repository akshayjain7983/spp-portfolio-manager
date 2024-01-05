package spp.portfolio.model.definition.configuration;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum Currency
{
    INR("INR", "Indian Rupee", "India");
    
    private final String code;
    private final String name;
    private final String region;
    
    private Currency(String symbol, String name, String region)
    {
        this.code = symbol;
        this.name = name;
        this.region = region;
    }
    
    public static Currency fromSymbol(String symbol)
    {
        switch (symbol)
        {
            case "INR": return INR;
            
            default:
                throw new IllegalArgumentException("Unexpected value: " + symbol);
        }
    }
}
