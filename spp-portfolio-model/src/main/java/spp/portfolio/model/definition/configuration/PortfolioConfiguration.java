package spp.portfolio.model.definition.configuration;

import lombok.Data;

@Data
public class PortfolioConfiguration
{
    private Currency currency;
    private Long portfolioAmountLimit;
    
    public PortfolioConfiguration(Currency currency, Long portfolioAmountLimit)
    {
        this.currency = currency;
        this.portfolioAmountLimit = portfolioAmountLimit;
    }
    
}
