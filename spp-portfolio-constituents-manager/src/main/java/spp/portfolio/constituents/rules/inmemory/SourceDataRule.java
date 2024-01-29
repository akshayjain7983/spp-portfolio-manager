package spp.portfolio.constituents.rules.inmemory;

import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

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
    private Collection<String> exchanges;
    private Collection<SecurityType> securityTypes;

    @Override
    public Collection<Security> execute(Collection<Security> securities, ConcurrentApplicationContext context)
    {
        Collection<Security> securitiesFromSource = new ArrayList<>();
        PortfolioRebalanceCommand command = context.fetch(portfolioRebalanceCommandKey);
        SecurityDataDao securityDataDao = context.fetch(securityDataDaoKey);
        ApplicationContext daoContext = new ApplicationContextImpl("SecurityDataDao");
        daoContext.add(Key.of("rebalanceDate", LocalDate.class), command.getDate());
        daoContext.add(Key.of("securityTypes", KeyType.<Collection<SecurityType>>of(Collection.class)), securityTypes);
        daoContext.add(Key.of("exchanges", KeyType.<Collection<String>>of(Collection.class)), exchanges);
        securitiesFromSource = securityDataDao.loadSecurities(daoContext);
        securitiesFromSource = updateDerivedData(securitiesFromSource, context);
        return securitiesFromSource;
    }

    private Collection<Security> updateDerivedData(Collection<Security> securities, ConcurrentApplicationContext context)
    {
        return new SecurityDerivedDataRule().execute(securities, context);
    }
}
