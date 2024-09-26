package spp.portfolio.constituents.rules.simple;

import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.breakLoop;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.isInnermostLoopIterationExhausted;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.isWeightCappingRun;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import lombok.Data;
import spp.portfolio.model.exception.SppException;

@Data
public class WeightCappingRule implements PortfolioRule
{
    private Collection<SecurityWeightCapper> securityWeightCappers;

    @Override
    public Collection<Security> execute(Collection<Security> securities, ConcurrentApplicationContext context)
    {
        Collection<Security> securitiesCapped = securities;
        context.add(isWeightCappingRun, new AtomicBoolean(false));
        
        for(SecurityWeightCapper capper:securityWeightCappers)
        {
            securitiesCapped = capper.capWeights(securitiesCapped, context);
        }
        
        AtomicBoolean weightCappingRun = context.erase(isWeightCappingRun);
        
        if(weightCappingRun.get() && isInnermostLoopIterationExhausted.apply(context))
            throw new SppException("Unable to cap weights. Review/adjust portfolio configuration.");
        
        if(!weightCappingRun.get())
            breakLoop.accept(context);//break LoopPortfolioRule if running since all weights are set
        
        return securitiesCapped;
    }
}
