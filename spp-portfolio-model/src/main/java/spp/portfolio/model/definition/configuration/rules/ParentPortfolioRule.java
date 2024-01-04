package spp.portfolio.model.definition.configuration.rules;

import lombok.Data;

@Data
public class ParentPortfolioRule implements PortfolioRule
{
    private Long parentPortfolioId;
}
