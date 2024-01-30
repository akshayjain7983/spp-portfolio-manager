package spp.portfolio.constituents.rules.inmemory;

import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.findPortfolioAmountLimit;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.findSumOfSecurityAttribute;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.safeDivide;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import spp.portfolio.constituents.rules.Security;

public class MarketValueSecurityWeightCalculator implements SecurityWeightCalculator
{
    @Override
    public Collection<Security> setupWeights(Collection<Security> securities, ConcurrentApplicationContext context)
    {
        setupMarketValue(securities);
        BigDecimal marketValueTotal = findSumOfSecurityAttribute.apply(securities, "market_value");        
        BigDecimal portfolioAmountLimit = findPortfolioAmountLimit.apply(context);
        BigDecimal deemedCash = portfolioAmountLimit.subtract(marketValueTotal);
        marketValueTotal = BigDecimal.ZERO.compareTo(deemedCash) < 0 ? marketValueTotal.add(deemedCash) : marketValueTotal;
        
        setupMarketValueWeight(securities, marketValueTotal);
        
        return securities;
    }

    private void setupMarketValueWeight(Collection<Security> securities, BigDecimal marketValueTotal)
    {
        if(BigDecimal.ZERO.compareTo(marketValueTotal) == 0)
            return;
        
        Optional.ofNullable(securities)
        .orElse(Collections.emptyList())
        .parallelStream()
        .forEach(security->{
            Optional<BigDecimal> marketValue = security.getAttributeValue("market_value", BigDecimal.class);
            Optional<BigDecimal> marketValueWeight = marketValue.map(mv->safeDivide.apply(mv, marketValueTotal));
            security.setAttributeValue("market_value_weight", marketValueWeight);
        });
    }

    private void setupMarketValue(Collection<Security> securities)
    {
        Optional.ofNullable(securities)
        .orElse(Collections.emptyList())
        .parallelStream()
        .forEach(security->{
            Optional<BigDecimal> rebalancePrice = 
                    security.getAttributeValue("rebalance_price", BigDecimal.class).or(()->{
                        security.setAttributeValue("rebalance_price", security.getAttributeValue("close_price", BigDecimal.class));
                        return security.getAttributeValue("rebalance_price", BigDecimal.class);
                    });
            
            
            Optional<Long> rebalanceUnits = 
                    security.getAttributeValue("rebalance_units", Long.class).or(()->{
                        security.setAttributeValue("rebalance_units", security.getAttributeValue("total_outstanding_shares", Long.class));
                        return security.getAttributeValue("rebalance_units", Long.class);
                    });
            
            Optional<BigDecimal> marketValue = rebalancePrice.flatMap(p->rebalanceUnits.map(u->p.multiply(BigDecimal.valueOf(u))));
            security.setAttributeValue("market_value", marketValue);
        });
    }

}
