package spp.portfolio.model.definition.configuration;

import lombok.Data;
import spp.portfolio.model.definition.configuration.constituents.PortfolioConfigurationConstituents;

@Data
public class PortfolioConfiguration
{
    private final Currency currency;
    private final Long portfolioAmountLimit;
    private PortfolioConfigurationConstituents portfolioConfigurationConstituents;
}
