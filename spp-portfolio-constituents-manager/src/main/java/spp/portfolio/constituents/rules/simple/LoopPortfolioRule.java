package spp.portfolio.constituents.rules.simple;

import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.isLoopContinueNextIteration;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.isLoopInnermost;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.loopRuleStatesKey;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.securitiesUniverseKey;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import lombok.Data;
import spp.portfolio.model.exception.SppException;

@Data
public class LoopPortfolioRule implements PortfolioRule
{
    private String loopLabel;
    private Integer maxIterations;
    private Collection<PortfolioRule> portfolioRules;
    private String startingUniverse;
    
    @Override
    public Collection<Security> execute(Collection<Security> securities, ConcurrentApplicationContext context)
    {
	
	if(StringUtils.isNotBlank(startingUniverse) && !StringUtils.equalsAny(startingUniverse, "PARENT", "SOURCE_UNIVERSE"))
	    throw new SppException("Invalid startingUniverse. Valid values are PARENT or SOURCE_UNIVERSE");
	
        initiateLooping(context);
        Collection<Security> securitiesLooped = StringUtils.equalsIgnoreCase(startingUniverse, "SOURCE_UNIVERSE") ? context.fetch(securitiesUniverseKey) : securities;
        
        RULE_LOOP: for(int iteration=1; iteration<=maxIterations; iteration++)
        {
            context.fetch(loopRuleStatesKey).peek().currentIteration().set(iteration);
            context.fetch(loopRuleStatesKey).peek().continueNextIteration().set(false);
            
            Iterator<PortfolioRule> portfolioRulesIterator = portfolioRules.iterator();
            while(portfolioRulesIterator.hasNext())
            {
                PortfolioRule rule = portfolioRulesIterator.next();
                securitiesLooped = rule.execute(securitiesLooped, context);
                
                if(isLoopContinueNextIteration.apply(loopLabel, context))
                    continue RULE_LOOP;
                
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
        
        context.fetch(loopRuleStatesKey).push(new LoopState(loopLabel, maxIterations, new AtomicInteger(0), new AtomicBoolean(false)));
    }
    
    public static final record LoopState(String loopLabel, Integer maxIterations, AtomicInteger currentIteration, AtomicBoolean continueNextIteration) {};
}
