package spp.portfolio.model.definition.configuration.rules;

import lombok.Data;

@Data
public class RankingRule implements PortfolioRule
{
    private Ranker ranker;
}
