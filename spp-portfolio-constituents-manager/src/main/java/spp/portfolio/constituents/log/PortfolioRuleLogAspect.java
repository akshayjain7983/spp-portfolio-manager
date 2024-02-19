package spp.portfolio.constituents.log;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import spp.portfolio.constituents.rules.inmemory.PortfolioRule;

//@Component
@Aspect
public class PortfolioRuleLogAspect
{
    @Pointcut("execution(* spp.portfolio.constituents.rules.inmemory.PortfolioRule.execute(..))")
    public void portfolioRuleExecute() {}
    
    @Before("portfolioRuleExecute() && target(portfolioRule) && args(securitiesIn, context,..)")
    public void logPortfolioRuleExecuteExecuteEntry(PortfolioRule portfolioRule, Collection<?> securitiesIn, ConcurrentApplicationContext context)
    {
	getLogger(portfolioRule).debug(()->"Entring execute with params --> total securities : %d | context : %s".formatted(securitiesIn.size(), context));
    }
    
    @AfterReturning(pointcut = "portfolioRuleExecute() && target(portfolioRule) && args(securitiesIn, context,..)", returning = "securitiesOut")
    public void logPortfolioRuleExecuteExecuteExit(PortfolioRule portfolioRule, Collection<?> securitiesIn, ConcurrentApplicationContext context, Collection<?> securitiesOut)
    {
	getLogger(portfolioRule).debug(()->"Exiting execute with return value --> total securities : %d | context : %s".formatted(securitiesOut.size(), context));
    }
    
    @AfterThrowing(pointcut = "portfolioRuleExecute() && target(portfolioRule) && args(securitiesIn, context,..)", throwing = "thrown")
    public void logPortfolioRuleExecuteExecuteExceptionally(PortfolioRule portfolioRule, Collection<?> securitiesIn, ConcurrentApplicationContext context, Exception thrown)
    {
	getLogger(portfolioRule).error(()->"Exiting execute with exception --> context : %s".formatted(context), thrown);
    }
    
    private Logger getLogger(Object target)
    {
	return LogManager.getLogger(target.getClass());
    }
}
