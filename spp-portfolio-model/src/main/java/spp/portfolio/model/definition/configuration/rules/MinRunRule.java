package spp.portfolio.model.definition.configuration.rules;

import lombok.Data;

@Data
public class MinRunRule implements PortfolioRule
{
    private Long minRunDays;
}
