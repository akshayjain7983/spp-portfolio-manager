package spp.portfolio.constituents.rules.inmemory;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

import lombok.Data;
import spp.portfolio.constituents.rules.SecurityType;

@Data
public class PortfolioConfiguration
{
    private final Currency currency;
    private final BigDecimal portfolioInvestmentAmountLimit;
    private final Boolean portfolioInvestmentAmountLimitUpdated;
    private Map<String, Collection<SecurityType>> exchangesWithSecurityTypes;
    private PortfolioConfigurationConstituents portfolioConfigurationConstituents;
}
