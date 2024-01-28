package spp.portfolio.model.spring;

import jakarta.persistence.Converter;
import spp.portfolio.manager.utilities.jpa.JsonJpaConverter;
import spp.portfolio.model.definition.configuration.PortfolioConfiguration;

@Converter(autoApply = true)
public class PortfolioConfigurationJpaConverter implements JsonJpaConverter<PortfolioConfiguration>
{
    @Override
    public Class<PortfolioConfiguration> getDataClass()
    {
        return PortfolioConfiguration.class;
    }
}
