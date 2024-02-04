package spp.portfolio.model.definition.configuration.rules;

import java.util.Collection;

import lombok.Data;

@Data
public class RankingRule implements PortfolioRule
{
    private Collection<Ranker> rankers;
}
