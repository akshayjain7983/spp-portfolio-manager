package spp.portfolio.constituents.rules.simple;

import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.portfolioDefinitionConfigurationKey;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.portfolioRebalanceCommandKey;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.portfolioRebalanceSecurityOutpointRepositorySupplier;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import spp.portfolio.constituents.rebalance.PortfolioRebalanceCommand;
import spp.portfolio.model.definition.PortfolioDefinitionConfiguration;
import spp.portfolio.model.rebalance.PortfolioRebalanceSecurityOutpoint;

@Aspect
public class SecurityOutpointTraceableAspect
{
    @Pointcut("target(spp.portfolio.constituents.rules.simple.SecuritiesOutpointTraceable)")
    public void securitiesOutpointTraceable() {}
    
    @Pointcut("target(spp.portfolio.constituents.rules.simple.SecurityOutpointTraceable)")
    public void securityOutpointTraceable() {}
    
    @Pointcut("execution(* spp.portfolio.constituents.rules.simple.SecurityWeightCapper.capWeights(..))")
    public void securityWeightCapperExecute() {}
    
    @Pointcut("execution(* spp.portfolio.constituents.rules.simple.PortfolioRule.execute(..))")
    public void portfolioRuleExecute() {}
    
    @Pointcut("execution(* spp.portfolio.constituents.rules.simple.Filter.execute(..))")
    public void filterExecute() {}
    
    @AfterReturning(pointcut = "(securityWeightCapperExecute() || portfolioRuleExecute()) && securitiesOutpointTraceable() && target(securitiesOutpointTraceable) && args(securitiesIn, context,..)", returning = "securitiesOut")
    public void outpointPortfolioRuleExecuteOrSecurityWeightCapperExecuteOnExit(SecuritiesOutpointTraceable securitiesOutpointTraceable, Collection<?> securitiesIn, ConcurrentApplicationContext context, Collection<?> securitiesOut)
    {
	if(!context.fetch(portfolioRebalanceCommandKey).getDebug())
	    return;
	
	if(CollectionUtils.isEmpty(securitiesIn))
	    return;
	
	Set<Security> securitiesRemoved = new HashSet<>((Collection<Security>)securitiesIn);
	securitiesRemoved.removeAll(Optional.ofNullable(securitiesOut).orElse(Collections.emptySet()));
	buildAndPersistOutpoints(securitiesRemoved, context, securitiesOutpointTraceable.getOutpointRepresentation());
    }
    
    @AfterReturning(pointcut = "filterExecute() && securityOutpointTraceable() && target(securityOutpointTraceable) && args(securityIn, context,..)", returning = "securityOut")
    public void outpointFilterExecuteOnExit(SecurityOutpointTraceable securityOutpointTraceable, Optional<Security> securityIn, ConcurrentApplicationContext context, Optional<Security> securityOut)
    {
	if(!context.fetch(portfolioRebalanceCommandKey).getDebug())
	    return;
	
	if(securityIn.isPresent() && securityOut.isEmpty())
	    buildAndPersistOutpoints(Set.of(securityIn.get()), context, securityOutpointTraceable.getOutpointRepresentation());
    }
    
    private void buildAndPersistOutpoints(Set<Security> securitiesRemoved, ConcurrentApplicationContext context, String securitiesOutpointRepresentation)
    {
	Collection<PortfolioRebalanceSecurityOutpoint> outpoints = 
		securitiesRemoved
		.stream()
		.map(s->getPortfolioRebalanceSecurityOutpoint(s, context, securitiesOutpointRepresentation))
		.collect(Collectors.toList());
	
	persistOutpoints(outpoints);
    }
    
    private void persistOutpoints(Collection<PortfolioRebalanceSecurityOutpoint> outpoints)
    {
	if(CollectionUtils.isNotEmpty(outpoints))
	    portfolioRebalanceSecurityOutpointRepositorySupplier.get().saveAll(outpoints);
    }

    private PortfolioRebalanceSecurityOutpoint getPortfolioRebalanceSecurityOutpoint(Security security, ConcurrentApplicationContext context, String outpointRepresentation)
    {
	PortfolioRebalanceCommand portfolioRebalanceCommand = context.fetch(portfolioRebalanceCommandKey);
	PortfolioDefinitionConfiguration portfolioDefinitionConfiguration = context.fetch(portfolioDefinitionConfigurationKey);
	return PortfolioRebalanceSecurityOutpoint.builder()
			.runId(portfolioRebalanceCommand.getRunId())
			.portfolioDefinitionId(portfolioRebalanceCommand.getPortfolioDefinitionId())
			.portfolioDefinitionConfigurationId(portfolioDefinitionConfiguration.getId())
			.rebalanceType(portfolioRebalanceCommand.getPortfolioRebalanceType())
			.rebalanceDate(portfolioRebalanceCommand.getDate())
			.securityId(security.getSecurityId())
			.outpoint(outpointRepresentation)
			.build();
    }
}
