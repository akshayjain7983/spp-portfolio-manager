package spp.portfolio.constituents.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import io.github.funofprograming.context.Key;
import io.github.funofprograming.context.KeyType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import spp.portfolio.configuration.expose.PortfolioConfigurationManager;
import spp.portfolio.constituents.rebalance.PortfolioRebalanceCommand;
import spp.portfolio.constituents.rules.Security;
import spp.portfolio.constituents.rules.SecurityType;
import spp.portfolio.constituents.rules.inmemory.LoopPortfolioRule;
import spp.portfolio.constituents.rules.inmemory.PortfolioConfiguration;
import spp.portfolio.constituents.rules.inmemory.dao.PortfolioRebalanceRepository;
import spp.portfolio.constituents.rules.inmemory.dao.SecurityDataDao;
import spp.portfolio.manager.utilities.spring.SpringContextHolder;
import spp.portfolio.model.definition.PortfolioDefinition;
import spp.portfolio.model.definition.PortfolioDefinitionConfiguration;
import spp.portfolio.model.exception.SppException;
import spp.portfolio.model.marketdata.dao.HolidayRepository;
import spp.portfolio.model.rebalance.PortfolioRebalance;
import spp.portfolio.model.rebalance.PortfolioRebalanceTransaction;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PortfolioConstituentsManagerConstants
{
    public static final Key<Collection<Security>> securitiesUniverseKey = Key.of("securitiesUniverse", KeyType.<Collection<Security>>of(Collection.class));
    public static final Key<ConcurrentMap<Long, String>> securityOutpointMapKey = Key.of("securityOutpointMap", KeyType.<ConcurrentMap<Long, String>>of(ConcurrentMap.class));
    public static final Key<PortfolioRebalanceCommand> portfolioRebalanceCommandKey = Key.of("portfolioRebalanceCommand", PortfolioRebalanceCommand.class);
    public static final Key<PortfolioRebalance> portfolioRebalanceKey = Key.of("portfolioRebalance", PortfolioRebalance.class);
    public static final Key<PortfolioDefinitionConfiguration> portfolioDefinitionConfigurationKey = Key.of("portfolioDefinitionConfiguration", PortfolioDefinitionConfiguration.class);
    public static final Key<PortfolioConfiguration> portfolioConfigurationKey = Key.of("portfolioConfiguration", PortfolioConfiguration.class);
    public static final Key<BlockingDeque<LoopPortfolioRule.LoopState>> loopRuleStatesKey = Key.of("loopRuleStates", KeyType.<BlockingDeque<LoopPortfolioRule.LoopState>>of(BlockingDeque.class));
    public static final Key<Collection<PortfolioRebalanceTransaction>> portfolioRebalanceTransactionsKey = Key.of("portfolioRebalanceTransactions", KeyType.<Collection<PortfolioRebalanceTransaction>>of(Collection.class));
    public static final Key<PortfolioRebalance> portfolioRebalanceLastKey = Key.of("portfolioRebalanceLast", KeyType.<PortfolioRebalance>of(PortfolioRebalance.class));
    public static final Key<BigDecimal> portfolioSizeCurrentKey = Key.of("portfolioSizeCurrent", KeyType.<BigDecimal>of(BigDecimal.class));
    
    public static final int bigDecimalScale = 64;
    
    public static final Function<PortfolioRebalanceCommand, String> rebalanceContextNameBuilder = prc -> String.join("--", prc.getPortfolioDefinitionId().toString(), prc.getPortfolioRebalanceType().name(), prc.getDate().toString());
    public static final BiFunction<String, ConcurrentApplicationContext, Boolean> isLoopInnermost = (loopLabel, context) -> StringUtils.equals(loopLabel, Optional.ofNullable(context.fetch(loopRuleStatesKey).peek()).map(ls->ls.loopLabel()).orElse(null));
    public static final Function<ConcurrentApplicationContext, Boolean> isInsideALoop = context -> Optional.ofNullable(context.fetch(loopRuleStatesKey)).map(s->!s.isEmpty()).orElse(Boolean.FALSE);
    public static final Consumer<ConcurrentApplicationContext> breakLoop = context -> Optional.of(isInsideALoop.apply(context)).filter(Boolean::booleanValue).ifPresent(b->context.fetch(loopRuleStatesKey).pop());
    public static final Function<ConcurrentApplicationContext, Boolean> isInnermostLoopIterationExhausted = context -> Optional.ofNullable(context.fetch(loopRuleStatesKey)).map(s->s.peek()).map(ls->ls.maxIterations().intValue() == ls.currentIteration().get()).orElse(Boolean.FALSE);
    public static final Function<ConcurrentApplicationContext, BigDecimal> findPortfolioInvestmentAmountLimit = context -> context.fetch(portfolioConfigurationKey).getPortfolioInvestmentAmountLimit();
    public static final BiFunction<Collection<Security>, String, BigDecimal> findSumOfSecurityAttribute = (securities, attr) -> Optional.ofNullable(securities).orElse(Collections.emptyList()).stream().map(s->s.getAttributeValue(attr, BigDecimal.class).orElse(null)).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);;
    public static final BinaryOperator<BigDecimal> safeDivide = (dividend, divisor) -> Optional.of(BigDecimal.ZERO).filter(z->z.compareTo(divisor)!=0).map(z->dividend.divide(divisor, bigDecimalScale, RoundingMode.HALF_UP)).orElse(BigDecimal.ZERO);
    public static final Supplier<SecurityDataDao> securityDataDaoSupplier = () -> SpringContextHolder.getBean(SecurityDataDao.class);
    public static final Supplier<PortfolioRebalanceRepository> portfolioRebalanceRepositorySupplier = () -> SpringContextHolder.getBean(PortfolioRebalanceRepository.class);
    public static final Supplier<HolidayRepository> holidayRepositorySupplier = () -> SpringContextHolder.getBean(HolidayRepository.class);
    public static final Supplier<PortfolioConfigurationManager> portfolioConfigurationManagerSupplier = () -> SpringContextHolder.getBean(PortfolioConfigurationManager.class);
    
    public static final BiFunction<Map<String, Collection<String>>, LocalDate, Boolean> isHolidayForAnyExchange =
            (exchangesWithSecurityTypes, date) -> {
                
                HolidayRepository holidayRepository = holidayRepositorySupplier.get();
                for(String exchange: exchangesWithSecurityTypes.keySet())
                {
                    for(String securityType:exchangesWithSecurityTypes.get(exchange))
                    {
                        if(holidayRepository.isHoliday(exchange, securityType, date))
                            return Boolean.TRUE;
                    }
                }
                
                return Boolean.FALSE;
            };
            
    public static final BiFunction<Map<String, Collection<SecurityType>>, LocalDate, LocalDate> findPreviousBusinessDate = 
            (exchangesWithSecurityTypes, currentDate) -> {
                
                LocalDate previousBusinessDate = currentDate.minusDays(1);
                Map<String, Collection<String>> exchangesWithSecurityTypesStr = exchangesWithSecurityTypes.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e->e.getValue().stream().map(SecurityType::name).collect(Collectors.toList())));
                while(isHolidayForAnyExchange.apply(exchangesWithSecurityTypesStr, previousBusinessDate))
                {
                    previousBusinessDate = previousBusinessDate.minusDays(1);
                }
                return previousBusinessDate;
            };
   
    public static final Function<PortfolioRebalanceCommand, PortfolioDefinitionConfiguration> findPortfolioDefinitionConfiguration = 
          (portfolioRebalanceCommand) -> {
              PortfolioConfigurationManager portfolioConfigurationManager = portfolioConfigurationManagerSupplier.get();
              PortfolioDefinition portfolioDefinition = portfolioConfigurationManager.getPortfolioDefinition(portfolioRebalanceCommand.getPortfolioDefinitionId()).orElseThrow(()->new SppException("Invalid portfolio definition id. No portfolio definition found"));
              LocalDate rebalanceDate = portfolioRebalanceCommand.getDate();
              if(rebalanceDate.isBefore(portfolioDefinition.getEffectiveDate()) || !rebalanceDate.isBefore(portfolioDefinition.getDiscontinuedDate()))
                  throw new SppException("Portfolio definition is not effective on rebalance date");
              
              return
                      portfolioDefinition.getPortfolioDefinitionConfigurations()
                      .stream()
                      .filter(pdc->rebalanceDate.isAfter(pdc.getValidFrom()) || (pdc.getValidFrom().isEqual(portfolioDefinition.getEffectiveDate()) && pdc.getValidFrom().isEqual(rebalanceDate)))
                      .filter(pdc->!rebalanceDate.isAfter(pdc.getValidTo()))
                      .findFirst()
                      .orElseThrow(()->new SppException("Portfolio definition does not have valid configuration for rebalance date"));
          };
}
