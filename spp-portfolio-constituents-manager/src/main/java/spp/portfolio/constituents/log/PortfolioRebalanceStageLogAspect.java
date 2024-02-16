package spp.portfolio.constituents.log;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import spp.portfolio.constituents.rebalance.PortfolioRebalanceCommand;
import spp.portfolio.constituents.rebalance.PortfolioRebalanceStage;
import spp.portfolio.model.definition.PortfolioDefinition;
import spp.portfolio.model.definition.PortfolioDefinitionConfiguration;
import spp.portfolio.model.rebalance.PortfolioRebalance;

@Component
@Aspect
public class PortfolioRebalanceStageLogAspect
{
    @Pointcut("execution(* spp.portfolio.constituents.rebalance.PortfolioRebalanceStage.execute(..)) && !target(spp.portfolio.constituents.rebalance.CompositePortfolioRebalanceStage)")
    public void portfolioRebalanceStageExecute() {}
    
    @Before("portfolioRebalanceStageExecute() && target(portfolioRebalanceStage) && args(portfolioRebalanceCommand,..)")
    public void logPortfolioRebalanceStageExecuteEntry(PortfolioRebalanceStage portfolioRebalanceStage, PortfolioRebalanceCommand portfolioRebalanceCommand)
    {
	getLogger(portfolioRebalanceStage).debug(()->"Entring execute with params : %s".formatted(portfolioRebalanceCommand));
    }
    
    @AfterReturning(pointcut = "portfolioRebalanceStageExecute() && target(portfolioRebalanceStage)", returning = "portfolioRebalance")
    public void logPortfolioRebalanceStageExecuteExit(PortfolioRebalanceStage portfolioRebalanceStage, PortfolioRebalance portfolioRebalance)
    {
	Optional<String> portfolioRebalanceForMessage = 
		Optional.ofNullable(portfolioRebalance)
		.map(pr->{
		    return
        		    pr.toBuilder()
        		    .portfolioDefinition(PortfolioDefinition.builder().id(pr.getPortfolioDefinition().getId()).name(pr.getPortfolioDefinition().getName()).build())
        		    .portfolioDefinitionConfiguration(PortfolioDefinitionConfiguration.builder().id(pr.getPortfolioDefinitionConfiguration().getId()).build())
        		    .portfolioConstituents(null)
        		    .portfolioRebalanceTransactions(null)
        		    .build()
        		    .toString();
		});
	
	getLogger(portfolioRebalanceStage).debug(()->"Exiting execute with return value : %s".formatted(portfolioRebalanceForMessage.orElse("null")));
    }
    
    @AfterThrowing(pointcut = "portfolioRebalanceStageExecute() && target(portfolioRebalanceStage)", throwing = "thrown")
    public void logPortfolioRebalanceStageExecuteExitExceptionally(PortfolioRebalanceStage portfolioRebalanceStage, Exception thrown)
    {
	getLogger(portfolioRebalanceStage).error(()->"Exiting execute with exception : ", thrown);
    }
    
    private Logger getLogger(Object target)
    {
	return LogManager.getLogger(target.getClass());
    }
}
