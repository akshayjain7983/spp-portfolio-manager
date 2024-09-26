package spp.portfolio.model.definition.configuration.rules;

import lombok.Data;

@Data
public class MaxSecuritiesCountRule implements PortfolioRule
{
    private Long maxSecuritesCount;
}
