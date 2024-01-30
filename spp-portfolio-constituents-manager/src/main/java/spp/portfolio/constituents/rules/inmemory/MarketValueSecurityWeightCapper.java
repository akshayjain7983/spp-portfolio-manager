package spp.portfolio.constituents.rules.inmemory;

import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.breakLoop;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.findPortfolioAmountLimit;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.findSumOfSecurityAttribute;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.isInnermostLoopIterationExhausted;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.safeDivide;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import lombok.Data;
import spp.portfolio.constituents.rules.Security;
import spp.portfolio.model.exception.SppException;

@Data
public class MarketValueSecurityWeightCapper implements SecurityWeightCapper
{
    private Map<String, BigDecimal> capWeightsByGroup;
    private WeightCappingStrategy weightCappingStrategy;
    
    @Override
    public Collection<Security> capWeights(Collection<Security> securities, ConcurrentApplicationContext context)
    {
        boolean weightCappingRun = false;
        BigDecimal portfolioAmountLimit = findPortfolioAmountLimit.apply(context);
        BigDecimal marketValueTotal = findSumOfSecurityAttribute.apply(securities, "market_value");
        for(String groupAttribute:capWeightsByGroup.keySet())
        {
            BigDecimal groupWeightCap = capWeightsByGroup.get(groupAttribute);
            Map<Object, Collection<Security>> securitiesGrouped = groupSecurities(securities, groupAttribute);
            
            for(Object groupVal:securitiesGrouped.keySet())
            {
                Collection<Security> groupedSecurities = securitiesGrouped.get(groupVal);
                BigDecimal weightOfGroup = findSumOfSecurityAttribute.apply(groupedSecurities, "market_value_weight");       
                
                if(weightOfGroup.compareTo(groupWeightCap) > 0 || marketValueTotal.compareTo(portfolioAmountLimit) > 0)
                {
                    weightCappingRun = true;
                    Map<Long, BigDecimal> existingDistributionMarketValue = groupedSecurities.stream().collect(Collectors.toMap(s->s.getSecurityId(), s->s.getAttributeValue("market_value", BigDecimal.class).orElse(BigDecimal.ZERO)));
                    BigDecimal groupPortfolioAmountLimit = portfolioAmountLimit.multiply(groupWeightCap);
                    Map<Long, BigDecimal> cappedDistributionMarketValue = weightCappingStrategy.capWeights(existingDistributionMarketValue, groupPortfolioAmountLimit);
                    groupedSecurities.stream().forEach(s->{
                        Optional<BigDecimal> cappedMv = Optional.ofNullable(cappedDistributionMarketValue.get(s.getSecurityId()));
                        Optional<BigDecimal> rebalancePrice = s.getAttributeValue("rebalance_price", BigDecimal.class);
                        Optional<Long> rebalanceUnits = cappedMv.flatMap(cmv->rebalancePrice.map(p->safeDivide.apply(cmv, p).longValue()));
                        s.setAttributeValue("rebalance_units", rebalanceUnits);
                    });
                }
            }
        }
        
        if(weightCappingRun && isInnermostLoopIterationExhausted.apply(context))
            throw new SppException("Unable to cap weights. Review/adjust portfolio configuration.");
        
        if(!weightCappingRun)
            breakLoop.accept(context); //break LoopPortfolioRule if running since all weights are set
        
        return securities;
    }

    private Map<Object, Collection<Security>> groupSecurities(Collection<Security> securities, String groupAttribute)
    {
        return Optional.ofNullable(securities)
                                .orElse(Collections.emptyList())
                                .stream()
                                .collect(Collectors.groupingBy(s->s.getAttributeValue(groupAttribute, Object.class), LinkedHashMap::new, Collectors.toCollection(ArrayList::new)));
    }
}
