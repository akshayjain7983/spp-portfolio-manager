package spp.portfolio.constituents.rules.inmemory;

import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.*;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.securityDataDaoSupplier;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.github.funofprograming.context.ApplicationContext;
import io.github.funofprograming.context.ConcurrentApplicationContext;
import io.github.funofprograming.context.Key;
import io.github.funofprograming.context.KeyType;
import io.github.funofprograming.context.impl.ApplicationContextImpl;
import lombok.Data;
import spp.portfolio.constituents.rebalance.PortfolioRebalanceCommand;
import spp.portfolio.constituents.rules.Security;
import spp.portfolio.constituents.rules.SecurityType;
import spp.portfolio.constituents.rules.inmemory.dao.SecurityDataDao;

@Data
public class SourceDataRule implements PortfolioRule
{
    @Override
    public Collection<Security> execute(Collection<Security> securities, ConcurrentApplicationContext context)
    {
        Collection<Security> securitiesFromSource = new ArrayList<>();
        PortfolioRebalanceCommand command = context.fetch(portfolioRebalanceCommandKey);
        PortfolioConfiguration portfolioConfiguration = context.fetch(portfolioConfigurationKey);
        Map<String, Collection<SecurityType>> exchangesWithSecurityTypes = portfolioConfiguration.getExchangesWithSecurityTypes();
        SecurityDataDao securityDataDao = securityDataDaoSupplier.get();
        ApplicationContext daoContext = new ApplicationContextImpl("SecurityDataDao");
        daoContext.add(Key.of("rebalanceDate", LocalDate.class), command.getDate());
        daoContext.add(Key.of("exchangesWithSecurityTypes", KeyType.<Map<String, Collection<SecurityType>>>of(Map.class)), exchangesWithSecurityTypes);
        securitiesFromSource = securityDataDao.loadSecurities(daoContext);
        securitiesFromSource = updateDerivedData(securitiesFromSource, context);
        context.add(securitiesUniverseKey, List.copyOf(securitiesFromSource));
        return securitiesFromSource;
    }

    private Collection<Security> updateDerivedData(Collection<Security> securities, ConcurrentApplicationContext context)
    {
        return new SecurityDerivedDataRule().execute(securities, context);
    }
}
