package spp.portfolio.model.spring;

import jakarta.persistence.Converter;
import spp.portfolio.manager.utilities.jpa.EnumJpaConverter;
import spp.portfolio.model.rebalance.PortfolioRebalanceTransactionType;

@Converter(autoApply = true)
public class PortfolioRebalanceTransactionTypeJpaConverter implements EnumJpaConverter<PortfolioRebalanceTransactionType>
{
    @Override
    public Class<PortfolioRebalanceTransactionType> getEnumClass()
    {
        return PortfolioRebalanceTransactionType.class;
    }
}
