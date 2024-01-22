package spp.portfolio.constituents.rules;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SecurityType
{
    EQUITY("Equity");
    
    private final String symbol;
    
    public static SecurityType getFromSymbol(String symbol)
    {
        switch (symbol)
        {
            case "Equity": return EQUITY;
            default:
                throw new IllegalArgumentException("Unexpected value: " + symbol);
        }
    }
}
