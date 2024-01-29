package spp.portfolio.model.definition.configuration.rules;

import java.util.Collection;

import lombok.Data;

@Data
public class WeightCalculationRule implements PortfolioRule
{
    private Collection<SecurityWeightCalculator> securityWeightCalculators;
    private Attribute<?> rebalanceWeight;
}
