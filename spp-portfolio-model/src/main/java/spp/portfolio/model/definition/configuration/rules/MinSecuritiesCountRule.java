package spp.portfolio.model.definition.configuration.rules;

import lombok.Data;

@Data
public class MinSecuritiesCountRule implements PortfolioRule
{
    private Long minSecuritesCount;
}
