package spp.portfolio.model.definition.configuration.constituents;

import java.time.Period;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import spp.portfolio.model.definition.configuration.rules.PortfolioRule;

@Data
@NoArgsConstructor
public class PortfolioConfigurationConstituents
{
    private List<PortfolioRule> constituentRules;
    private Period rebalancingFrequency;
}
