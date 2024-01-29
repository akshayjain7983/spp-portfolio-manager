package spp.portfolio.model.definition.configuration.rules;

import java.util.Collection;

import lombok.Data;

@Data
public class LoopPortfolioRule implements PortfolioRule
{
    private String loopLabel;
    private Integer maxIterations;
    private Collection<PortfolioRule> portfolioRules;
}
