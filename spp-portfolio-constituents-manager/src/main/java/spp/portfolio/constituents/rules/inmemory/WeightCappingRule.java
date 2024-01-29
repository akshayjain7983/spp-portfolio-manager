package spp.portfolio.constituents.rules.inmemory;

import java.util.Collection;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import lombok.Data;
import spp.portfolio.constituents.rules.Security;

@Data
public class WeightCappingRule implements PortfolioRule
{
    private Collection<SecurityWeightCapper> securityWeightCappers;

    @Override
    public Collection<Security> execute(Collection<Security> securities, ConcurrentApplicationContext context)
    {
        Collection<Security> securitiesCapped = securities;
        
        for(SecurityWeightCapper capper:securityWeightCappers)
        {
            securitiesCapped = capper.capWeights(securitiesCapped, context);
        }
        return securitiesCapped;
    }
}
