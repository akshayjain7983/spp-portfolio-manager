package spp.portfolio.model.definition.configuration.rules;

import java.util.Collection;

import lombok.Data;

@Data
public class SourceDataRule implements PortfolioRule
{
    private Collection<String> exchanges;
    private Collection<SecurityType> securityTypes;
}
