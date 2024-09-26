package spp.portfolio.constituents.rules.simple;

import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.portfolioRebalanceKey;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.portfolioRebalanceLastKey;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.portfolioRebalanceTransactionsKey;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.securitiesUniverseKey;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import spp.portfolio.model.rebalance.PortfolioConstituent;
import spp.portfolio.model.rebalance.PortfolioRebalance;
import spp.portfolio.model.rebalance.PortfolioRebalanceTransaction;
import spp.portfolio.model.rebalance.PortfolioRebalanceTransactionType;

public class PortfolioTrasactionsRule implements PortfolioRule
{
    @Override
    public Collection<Security> execute(Collection<Security> securities, ConcurrentApplicationContext context)
    {
        Optional<PortfolioRebalance> portfolioRebalancePrevious = Optional.ofNullable(context.fetch(portfolioRebalanceLastKey));
        setupPortfolioTrasactions(securities, context, portfolioRebalancePrevious);
        return securities;
    }

    private void setupPortfolioTrasactions(Collection<Security> securities, ConcurrentApplicationContext context, Optional<PortfolioRebalance> portfolioRebalancePrevious)
    {
        Collection<PortfolioRebalanceTransaction> portfolioRebalanceTransactions = new ArrayList<>();
        PortfolioRebalance portfolioRebalance = context.fetch(portfolioRebalanceKey);
        Collection<Security> securitiesUniverse = context.fetch(securitiesUniverseKey); 
        Map<Long, BigDecimal> securitiesUniversePriceRebalanceDate = 
                Optional.ofNullable(securitiesUniverse)
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(Security::getSecurityId, s->s.getAttributeValue("rebalance_price", BigDecimal.class).or(()->s.getAttributeValue("close_price", BigDecimal.class)).orElse(BigDecimal.ZERO)));
        
        Map<Long, Long> portfolioConstituentUnitsPrevious = 
                portfolioRebalancePrevious
                .map(PortfolioRebalance::getPortfolioConstituents)
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(PortfolioConstituent::getSecurityId, PortfolioConstituent::getUnits));
        
        Map<Long, Long> portfolioConstituentUnitsRebalanceDate = 
                Optional.ofNullable(securities)
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(Security::getSecurityId, s->s.getAttributeValue("rebalance_units", Long.class).orElse(0L)));
        
        //new or changed vs last
        for(Long securityId:portfolioConstituentUnitsRebalanceDate.keySet())
        {
            Long unitsRebalanceDate = portfolioConstituentUnitsRebalanceDate.get(securityId);
            Long unitsPreviousRebalanceDate = portfolioConstituentUnitsPrevious.get(securityId);
            Long difference = unitsRebalanceDate - Optional.ofNullable(unitsPreviousRebalanceDate).orElse(0L);
            
            if(difference == 0L)
                continue;
            
            Long transactionUnits = Math.abs(difference);
            PortfolioRebalanceTransactionType transactionType = difference > 0 ? PortfolioRebalanceTransactionType.BUY : PortfolioRebalanceTransactionType.SELL;
            BigDecimal transactionPrice = securitiesUniversePriceRebalanceDate.get(securityId);
            PortfolioRebalanceTransaction transaction = PortfolioRebalanceTransaction.builder()
                                                                                                                                .portfolioRebalance(portfolioRebalance)
                                                                                                                                .securityId(securityId)
                                                                                                                                .transactionType(transactionType)
                                                                                                                                .price(transactionPrice)
                                                                                                                                .units(transactionUnits)
                                                                                                                                .build();
            portfolioRebalanceTransactions.add(transaction);
        }
        
        //old that do not exist in new must be sell transaction
        for(Long securityId:portfolioConstituentUnitsPrevious.keySet())
        {
            if(!portfolioConstituentUnitsRebalanceDate.containsKey(securityId))
            {
                Long unitsPreviousRebalanceDate = portfolioConstituentUnitsPrevious.get(securityId);
                PortfolioRebalanceTransactionType transactionType = PortfolioRebalanceTransactionType.SELL;
                BigDecimal transactionPrice = securitiesUniversePriceRebalanceDate.get(securityId);
                PortfolioRebalanceTransaction transaction = PortfolioRebalanceTransaction.builder()
                                                                                                                                    .portfolioRebalance(portfolioRebalance)
                                                                                                                                    .securityId(securityId)
                                                                                                                                    .transactionType(transactionType)
                                                                                                                                    .price(transactionPrice)
                                                                                                                                    .units(unitsPreviousRebalanceDate)
                                                                                                                                    .build();
                portfolioRebalanceTransactions.add(transaction);
            }
        }
        
        context.add(portfolioRebalanceTransactionsKey, portfolioRebalanceTransactions);
    }
}
