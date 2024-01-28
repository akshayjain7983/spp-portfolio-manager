package spp.portfolio.model.spring;

import jakarta.persistence.Converter;
import spp.portfolio.manager.utilities.jpa.EnumJpaConverter;
import spp.portfolio.model.rebalance.PortfolioRebalanceType;

@Converter(autoApply = true)
public class PortfolioRebalanceTypeJpaConverter implements EnumJpaConverter<PortfolioRebalanceType>
{
    @Override
    public Class<PortfolioRebalanceType> getEnumClass()
    {
        return PortfolioRebalanceType.class;
    }
}
