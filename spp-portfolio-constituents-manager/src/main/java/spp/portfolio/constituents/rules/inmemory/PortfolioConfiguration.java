package spp.portfolio.constituents.rules.inmemory;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PortfolioConfiguration
{
    private final Currency currency;
    private final BigDecimal portfolioAmountLimit;
    private PortfolioConfigurationConstituents portfolioConfigurationConstituents;
}
