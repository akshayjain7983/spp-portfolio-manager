package spp.portfolio.model.definition.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = "symbol")
public class Currency
{
    private String symbol;
    private String name;
    private String region;
    
    public Currency(String symbol)
    {
        this(symbol, null, null);
    }
}
