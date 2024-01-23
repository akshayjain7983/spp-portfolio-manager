package spp.portfolio.model.spring;

import static spp.portfolio.manager.utilities.json.JsonUtil.fromJson;
import static spp.portfolio.manager.utilities.json.JsonUtil.toJson;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import spp.portfolio.model.definition.configuration.PortfolioConfiguration;

@Converter(autoApply = true)
public class PortfolioConfigurationJpaConverter implements AttributeConverter<PortfolioConfiguration, String>
{
    @Override
    public String convertToDatabaseColumn(PortfolioConfiguration attribute)
    {
        return toJson(attribute);
    }

    @Override
    public PortfolioConfiguration convertToEntityAttribute(String dbData)
    {
        return fromJson(dbData, PortfolioConfiguration.class);
    }
}
