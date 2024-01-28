package spp.portfolio.model.definition.configuration;

import java.math.BigDecimal;

import lombok.Data;
import spp.portfolio.model.definition.configuration.constituents.PortfolioConfigurationConstituents;

@Data
public class PortfolioConfiguration
{
    private final Currency currency;
    private final BigDecimal portfolioAmountLimit;
    private PortfolioConfigurationConstituents portfolioConfigurationConstituents;
}
