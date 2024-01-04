package spp.portfolio.model.definition.configuration.rules;

import lombok.Data;

@Data
public class SourceDataRule implements PortfolioRule
{
    private String exchange;
    private SecurityType securityType;
}
