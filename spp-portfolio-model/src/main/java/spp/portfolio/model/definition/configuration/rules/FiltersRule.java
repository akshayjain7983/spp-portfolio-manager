package spp.portfolio.model.definition.configuration.rules;

import lombok.Data;

@Data
public class FiltersRule implements PortfolioRule
{
    private CompoundFilter filter;
}
