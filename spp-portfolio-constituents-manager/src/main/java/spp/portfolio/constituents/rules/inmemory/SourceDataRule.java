package spp.portfolio.constituents.rules.inmemory;

import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.findPortfolioInvestmentAmountLimit;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.findPreviousBusinessDate;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.portfolioConfigurationKey;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.portfolioDefinitionConfigurationKey;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.portfolioRebalanceCommandKey;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.portfolioRebalanceLastKey;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.portfolioRebalanceRepositorySupplier;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.portfolioSizeCurrentKey;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.securitiesUniverseKey;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.securityDataDaoSupplier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import io.github.funofprograming.context.ApplicationContext;
import io.github.funofprograming.context.ConcurrentApplicationContext;
import io.github.funofprograming.context.Key;
import io.github.funofprograming.context.KeyType;
import io.github.funofprograming.context.impl.ApplicationContextImpl;
import lombok.Data;
import spp.portfolio.constituents.rebalance.PortfolioRebalanceCommand;
import spp.portfolio.constituents.rules.Security;
import spp.portfolio.constituents.rules.SecurityType;
import spp.portfolio.constituents.rules.inmemory.dao.PortfolioRebalanceRepository;
import spp.portfolio.constituents.rules.inmemory.dao.SecurityDataDao;
import spp.portfolio.model.definition.PortfolioDefinition;
import spp.portfolio.model.definition.PortfolioDefinitionConfiguration;
import spp.portfolio.model.rebalance.PortfolioRebalance;
import spp.portfolio.model.rebalance.PortfolioRebalanceType;

@Data
public class SourceDataRule implements PortfolioRule
{
    @Override
    public Collection<Security> execute(Collection<Security> securities, ConcurrentApplicationContext context)
    {
        Collection<Security> securitiesFromSource = loadSecuritiesCurrentData(securities, context);        
        securitiesFromSource = updateDerivedData(securitiesFromSource, context);
        setupPreviousRebalance(context);
        setupPortfolioSizeCurrent(context);
        return securitiesFromSource;
    }
    
    private Collection<Security> loadSecuritiesCurrentData(Collection<Security> securities, ConcurrentApplicationContext context)
    {
        PortfolioRebalanceCommand command = context.fetch(portfolioRebalanceCommandKey);
        PortfolioConfiguration portfolioConfiguration = context.fetch(portfolioConfigurationKey);
        Map<String, Collection<SecurityType>> exchangesWithSecurityTypes = portfolioConfiguration.getExchangesWithSecurityTypes();
        SecurityDataDao securityDataDao = securityDataDaoSupplier.get();
        ApplicationContext daoContext = new ApplicationContextImpl("SecurityDataDao");
        daoContext.add(Key.of("rebalanceDate", LocalDate.class), command.getDate());
        daoContext.add(Key.of("exchangesWithSecurityTypes", KeyType.<Map<String, Collection<SecurityType>>>of(Map.class)), exchangesWithSecurityTypes);
        Collection<Security> securitiesFromSource = securityDataDao.loadSecurities(daoContext);
        context.add(securitiesUniverseKey, List.copyOf(securitiesFromSource));
        return securitiesFromSource;
    }

    private Collection<Security> updateDerivedData(Collection<Security> securities, ConcurrentApplicationContext context)
    {
        return new SecurityDerivedDataRule().execute(securities, context);
    }
    
    private void setupPreviousRebalance(ConcurrentApplicationContext context)
    {
        PortfolioRebalanceCommand command = context.fetch(portfolioRebalanceCommandKey);
        PortfolioConfiguration portfolioConfiguration = context.fetch(portfolioConfigurationKey);
        PortfolioRebalanceRepository portfolioRebalanceRepository = portfolioRebalanceRepositorySupplier.get();
        PortfolioDefinition portfolioDefinition = context.fetch(portfolioDefinitionConfigurationKey).getPortfolioDefinition();
        PortfolioRebalanceType portfolioRebalanceType = command.getPortfolioRebalanceType();
        LocalDate rebalanceDate = command.getDate();
        LocalDate previousBusinessDate = findPreviousBusinessDate.apply(portfolioConfiguration.getExchangesWithSecurityTypes(), rebalanceDate);
        PortfolioRebalance portfolioRebalanceLast = portfolioRebalanceRepository.findByPortfolioDefinitionAndDateAndRebalanceTypeAndIsActive(portfolioDefinition, previousBusinessDate, portfolioRebalanceType, Boolean.TRUE).orElse(null);
        context.add(portfolioRebalanceLastKey, portfolioRebalanceLast);
    }
    
    private void setupPortfolioSizeCurrent(ConcurrentApplicationContext context)
    {
        BigDecimal portfolioInvestmentAmountLimit = findPortfolioInvestmentAmountLimit.apply(context);
        PortfolioDefinitionConfiguration portfolioDefinitionConfiguration = context.fetch(portfolioDefinitionConfigurationKey);
        PortfolioRebalanceCommand portfolioRebalanceCommand = context.fetch(portfolioRebalanceCommandKey);
        LocalDate rebalanceDate = portfolioRebalanceCommand.getDate();
        LocalDate portfolioStartDate = portfolioDefinitionConfiguration.getPortfolioDefinition().getEffectiveDate();
        LocalDate portfolioConfigValidFrom = portfolioDefinitionConfiguration.getValidFrom();
        Boolean portfolioInvestmentAmountLimitUpdated = portfolioDefinitionConfiguration.getConfiguration().getPortfolioInvestmentAmountLimitUpdated();
        BigDecimal portfolioSizeCurrent = portfolioInvestmentAmountLimit;
        
        if(!rebalanceDate.isEqual(portfolioStartDate)
                && !(rebalanceDate.isEqual(portfolioConfigValidFrom) && portfolioInvestmentAmountLimitUpdated))
        {
            Optional<PortfolioRebalance> portfolioRebalanceLast = Optional.ofNullable(context.fetch(portfolioRebalanceLastKey));
            BigDecimal portfolioRebalanceLastCash = portfolioRebalanceLast.map(PortfolioRebalance::getPortfolioCash).orElse(BigDecimal.ZERO);
            
            Collection<Security> securitiesUniverse = context.fetch(securitiesUniverseKey);
            
            Map<Long, BigDecimal> securitiesUniversePriceRebalanceDate = 
                    Optional.ofNullable(securitiesUniverse)
                    .orElse(Collections.emptyList())
                    .stream()
                    .collect(Collectors.toMap(Security::getSecurityId, s->s.getAttributeValue("close_price", BigDecimal.class).orElse(BigDecimal.ZERO)));
            
            BigDecimal portfolioRebalanceLastCurrentValue =
                    portfolioRebalanceLast
                    .map(pr->pr.getPortfolioConstituents())
                    .filter(CollectionUtils::isNotEmpty)
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(c->securitiesUniversePriceRebalanceDate.get(c.getSecurityId()).multiply(BigDecimal.valueOf(c.getUnits())))
                    .reduce(portfolioRebalanceLastCash, BigDecimal::add);
            
            portfolioSizeCurrent = portfolioRebalanceLastCurrentValue;
        }
        
        context.add(portfolioSizeCurrentKey, portfolioSizeCurrent);
    }
}
