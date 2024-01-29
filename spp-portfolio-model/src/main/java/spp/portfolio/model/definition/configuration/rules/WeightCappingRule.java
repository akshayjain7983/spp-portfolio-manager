package spp.portfolio.model.definition.configuration.rules;

import java.util.Collection;

import lombok.Data;

@Data
public class WeightCappingRule implements PortfolioRule
{
    private Collection<SecurityWeightCapper> securityWeightCappers;
}
