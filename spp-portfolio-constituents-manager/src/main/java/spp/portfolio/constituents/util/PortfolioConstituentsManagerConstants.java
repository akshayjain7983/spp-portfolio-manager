package spp.portfolio.constituents.util;

import java.util.Optional;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import io.github.funofprograming.context.Key;
import io.github.funofprograming.context.KeyType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import spp.portfolio.constituents.rebalance.PortfolioRebalanceCommand;
import spp.portfolio.constituents.rules.inmemory.LoopPortfolioRule;
import spp.portfolio.constituents.rules.inmemory.PortfolioConfiguration;
import spp.portfolio.constituents.rules.inmemory.dao.SecurityDataDao;
import spp.portfolio.model.definition.PortfolioDefinitionConfiguration;
import spp.portfolio.model.rebalance.PortfolioRebalance;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PortfolioConstituentsManagerConstants
{
    public static final Key<ConcurrentMap<Long, String>> securityOutpointMapKey = Key.of("securityOutpointMapKey", KeyType.<ConcurrentMap<Long, String>>of(ConcurrentMap.class));
    public static final Function<PortfolioRebalanceCommand, String> rebalanceContextNameBuilder = prc -> String.join("--", prc.getPortfolioDefinitionId().toString(), prc.getPortfolioRebalanceType().name(), prc.getDate().toString());
    public static final Key<PortfolioRebalanceCommand> portfolioRebalanceCommandKey = Key.of("portfolioRebalanceCommand", PortfolioRebalanceCommand.class);
    public static final Key<PortfolioRebalance> portfolioRebalanceKey = Key.of("portfolioRebalance", PortfolioRebalance.class);
    public static final Key<PortfolioDefinitionConfiguration> portfolioDefinitionConfigurationKey = Key.of("portfolioDefinitionConfiguration", PortfolioDefinitionConfiguration.class);
    public static final Key<SecurityDataDao> securityDataDaoKey = Key.of("securityDataDao", SecurityDataDao.class);
    public static final Key<PortfolioConfiguration> portfolioConfigurationKey = Key.of("portfolioConfiguration", PortfolioConfiguration.class);
    public static final Key<BlockingDeque<LoopPortfolioRule.LoopState>> loopRuleStatesKey = Key.of("loopRuleStates", KeyType.<BlockingDeque<LoopPortfolioRule.LoopState>>of(BlockingDeque.class));
    public static final BiFunction<String, ConcurrentApplicationContext, Boolean> isLoopInnermost = (loopLabel, context) -> StringUtils.equals(loopLabel, Optional.ofNullable(context.fetch(loopRuleStatesKey).peek()).map(ls->ls.loopLabel()).orElse(null));
    public static final Function<ConcurrentApplicationContext, Boolean> isInsideALoop = context -> Optional.ofNullable(context.fetch(loopRuleStatesKey)).map(s->!s.isEmpty()).orElse(Boolean.FALSE);
    public static final Consumer<ConcurrentApplicationContext> breakLoop = context -> Optional.of(isInsideALoop.apply(context)).filter(Boolean::booleanValue).ifPresent(b->context.fetch(loopRuleStatesKey).pop());
}
