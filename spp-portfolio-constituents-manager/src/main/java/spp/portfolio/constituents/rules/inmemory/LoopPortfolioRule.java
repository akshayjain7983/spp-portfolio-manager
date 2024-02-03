package spp.portfolio.constituents.rules.inmemory;

import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.isLoopInnermost;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.loopRuleStatesKey;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import lombok.Data;
import spp.portfolio.constituents.rules.Security;

@Data
public class LoopPortfolioRule implements PortfolioRule
{
    private String loopLabel;
    private Integer maxIterations;
    private Collection<PortfolioRule> portfolioRules;
    
    @Override
    public Collection<Security> execute(Collection<Security> securities, ConcurrentApplicationContext context)
    {
        initiateLooping(context);
        Collection<Security> securitiesLooped = securities;
        
        RULE_LOOP: for(int iteration=1; iteration<=maxIterations; iteration++)
        {
            context.fetch(loopRuleStatesKey).peek().currentIteration().set(iteration);
            Iterator<PortfolioRule> portfolioRulesIterator = portfolioRules.iterator();
            while(portfolioRulesIterator.hasNext())
            {
                PortfolioRule rule = portfolioRulesIterator.next();
                securitiesLooped = rule.execute(securitiesLooped, context);
                
                if(!isLoopInnermost.apply(loopLabel, context))
                    break RULE_LOOP;
            }
        }
        
        if(isLoopInnermost.apply(loopLabel, context))
            context.fetch(loopRuleStatesKey).pop();        
        
        return securitiesLooped;
    }

    private void initiateLooping(ConcurrentApplicationContext context)
    {
        if(Objects.isNull(context.fetch(loopRuleStatesKey)))
            context.add(loopRuleStatesKey, new LinkedBlockingDeque<>());
        
        context.fetch(loopRuleStatesKey).push(new LoopState(loopLabel, maxIterations, new AtomicInteger(0)));
    }
    
    public static final record LoopState(String loopLabel, Integer maxIterations, AtomicInteger currentIteration) {};
}
