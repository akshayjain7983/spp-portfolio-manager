package spp.portfolio.model.definition.configuration;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

import lombok.Data;
import spp.portfolio.model.definition.configuration.constituents.PortfolioConfigurationConstituents;
import spp.portfolio.model.definition.configuration.rules.SecurityType;

@Data
public class PortfolioConfiguration
{
    private final Currency currency;
    private final BigDecimal portfolioAmountLimit;
    private Map<String, Collection<SecurityType>> exchangesWithSecurityTypes;
    private PortfolioConfigurationConstituents portfolioConfigurationConstituents;
}
