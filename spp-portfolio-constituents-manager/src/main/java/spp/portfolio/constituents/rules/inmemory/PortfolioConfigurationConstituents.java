package spp.portfolio.constituents.rules.inmemory;

import java.time.Period;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import lombok.Data;
import lombok.NoArgsConstructor;
import spp.portfolio.constituents.rules.Security;

@Data
@NoArgsConstructor
public class PortfolioConfigurationConstituents
{
    private List<PortfolioRule> constituentRules;
    private Period rebalancingFrequency;
    
    public Collection<Security> execute(ConcurrentApplicationContext context)
    {
        Collection<Security> results = Collections.emptyList();
        for(PortfolioRule rule: constituentRules)
        {
            if(rule.doExecute(context))
            {
                results = rule.execute(results, context);
            }
        }
        
        return results;
    }
}
