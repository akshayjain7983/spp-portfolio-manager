package spp.portfolio.model.definition.configuration.rules;

import lombok.Data;

@Data
public class MaxSecuritiesRule implements PortfolioRule
{
    private Long maxSecurites;
}
