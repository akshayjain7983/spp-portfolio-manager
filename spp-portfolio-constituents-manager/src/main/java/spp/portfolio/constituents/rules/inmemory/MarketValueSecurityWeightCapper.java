package spp.portfolio.constituents.rules.inmemory;

import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.*;

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
        for(String groupAttribute:capWeightsByGroup.keySet())
        {
            BigDecimal groupWeightCap = capWeightsByGroup.get(groupAttribute);
            Map<Object, Collection<Security>> securitiesGrouped = groupSecurities(securities, groupAttribute);
            
            for(Object groupVal:securitiesGrouped.keySet())
            {
                Collection<Security> groupedSecurities = securitiesGrouped.get(groupVal);
                BigDecimal weightOfGroup = 
                        Optional.ofNullable(groupedSecurities)
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(s->s.getAttributeValue("market_value_weight", BigDecimal.class).orElse(BigDecimal.ZERO))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                if(weightOfGroup.compareTo(groupWeightCap)>0)
                {
                    weightCappingRun = true;
                    Map<Long, BigDecimal> existingDistributionMarketValue = securities.stream().collect(Collectors.toMap(s->s.getSecurityId(), s->s.getAttributeValue("market_value", BigDecimal.class).orElse(BigDecimal.ZERO)));
                    BigDecimal groupPortfolioAmountLimit = portfolioAmountLimit.multiply(groupWeightCap);
                    Map<Long, BigDecimal> cappedDistributionMarketValue = weightCappingStrategy.capWeights(existingDistributionMarketValue, groupPortfolioAmountLimit);
                    securities.stream().forEach(s->{
                        Optional<BigDecimal> cappedMv = Optional.ofNullable(cappedDistributionMarketValue.get(s.getSecurityId()));
                        Optional<BigDecimal> rebalancePrice = s.getAttributeValue("rebalance_price", BigDecimal.class);
                        Optional<Long> rebalanceUnits = cappedMv.flatMap(cmv->rebalancePrice.map(p->cmv.divide(p).longValue()));
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
