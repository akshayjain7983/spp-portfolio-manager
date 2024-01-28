package spp.portfolio.constituents.util;

import java.util.Map;
import java.util.function.Function;

import io.github.funofprograming.context.Key;
import io.github.funofprograming.context.KeyType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import spp.portfolio.constituents.rebalance.PortfolioRebalanceCommand;
import spp.portfolio.constituents.rules.inmemory.PortfolioConfiguration;
import spp.portfolio.constituents.rules.inmemory.dao.SecurityDataDao;
import spp.portfolio.model.definition.PortfolioDefinitionConfiguration;
import spp.portfolio.model.rebalance.PortfolioRebalance;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PortfolioConstituentsManagerConstants
{
    public static final Key<Map<Long, String>> securityOutpointMapKey = Key.of("securityOutpointMapKey", KeyType.<Map<Long, String>>of(Map.class));
    public static final Function<PortfolioRebalanceCommand, String> rebalanceContextNameBuilder = prc->String.join("--", prc.getPortfolioDefinitionId().toString(), prc.getPortfolioRebalanceType().name(), prc.getDate().toString());
    public static final Key<PortfolioRebalanceCommand> portfolioRebalanceCommandKey = Key.of("portfolioRebalanceCommand", PortfolioRebalanceCommand.class);
    public static final Key<PortfolioRebalance> portfolioRebalanceKey = Key.of("portfolioRebalance", PortfolioRebalance.class);
    public static final Key<PortfolioDefinitionConfiguration> portfolioDefinitionConfigurationKey = Key.of("portfolioDefinitionConfiguration", PortfolioDefinitionConfiguration.class);
    public static final Key<SecurityDataDao> securityDataDaoKey = Key.of("securityDataDao", SecurityDataDao.class);
    public static final Key<PortfolioConfiguration> portfolioConfigurationKey = Key.of("portfolioConfiguration", PortfolioConfiguration.class);
}
