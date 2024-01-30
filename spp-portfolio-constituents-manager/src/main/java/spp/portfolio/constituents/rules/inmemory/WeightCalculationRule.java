package spp.portfolio.constituents.rules.inmemory;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import lombok.Data;
import spp.portfolio.constituents.rules.Security;

@Data
public class WeightCalculationRule implements PortfolioRule
{
    private Collection<SecurityWeightCalculator> securityWeightCalculators;
    private String rebalanceWeightAttributeName;
    
    @Override
    public Collection<Security> execute(Collection<Security> securities, ConcurrentApplicationContext context)
    {
        Collection<Security> securitiesWeighted = securities;
        for(SecurityWeightCalculator weightCalculator: securityWeightCalculators)
        {
            securitiesWeighted = weightCalculator.setupWeights(securitiesWeighted, context);
        }
        
        Optional.ofNullable(securitiesWeighted)
        .orElse(Collections.emptyList())
        .parallelStream()
        .forEach(security->{
            Optional<BigDecimal> calculatedWeight = security.getAttributeValue(rebalanceWeightAttributeName, BigDecimal.class);
            security.setAttributeValue("rebalance_weight", calculatedWeight);
        });
        
        return securitiesWeighted;
    }
}
