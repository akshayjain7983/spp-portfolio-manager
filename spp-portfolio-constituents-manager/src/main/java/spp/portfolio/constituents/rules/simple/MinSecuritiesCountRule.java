package spp.portfolio.constituents.rules.simple;

import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.continueLoop;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.isInnermostLoopIterationExhausted;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.relaxationCondition;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import lombok.Data;
import spp.portfolio.constituents.rules.RelaxationCondition;
import spp.portfolio.model.exception.SppException;

@Data
public class MinSecuritiesCountRule implements PortfolioRule
{
    private Long minSecuritesCount;

    @Override
    public Collection<Security> execute(Collection<Security> securities, ConcurrentApplicationContext context)
    {
	Long currentSecuritiesCount = (long) Optional.ofNullable(securities).orElse(Collections.emptyList()).size();
	RelaxationCondition relaxationApplied = context.fetch(relaxationCondition);
	boolean minCountAchieved = currentSecuritiesCount >= minSecuritesCount;	
	
	if(
		(!minCountAchieved && Objects.nonNull(relaxationApplied) && RelaxationCondition.MIN_COUNT == relaxationApplied)
		||
		(!minCountAchieved && isInnermostLoopIterationExhausted.apply(context))
	)
	{
	    throw new SppException("Unable to achieve min securities count. Review/adjust portfolio configuration."); 
	}
	else if(!minCountAchieved)
	{
	    
	    context.add(relaxationCondition, RelaxationCondition.MIN_COUNT); //relax filters if set in config. If not set then this will iterate over loop and fail eventually
	    continueLoop.accept(context); //min count not met do not execute any more rules like weight capping etc
	}
	else 
	{
	    context.erase(relaxationCondition); //all good no more relaxation
	}
	    
	return securities;
    }

}
