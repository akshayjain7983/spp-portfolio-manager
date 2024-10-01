//package spp.portfolio.constituents.rules.simple;
//
//import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.*;
//
//import java.util.Optional;
//
//import io.github.funofprograming.context.ConcurrentApplicationContext;
//import spp.portfolio.constituents.rebalance.PortfolioRebalanceCommand;
//import spp.portfolio.model.definition.PortfolioDefinitionConfiguration;
//import spp.portfolio.model.rebalance.PortfolioRebalanceSecurityOutpoint;
//
//public abstract class OutpointTraceableFilter implements Filter
//{
//
//    @Override
//    public Optional<Security> execute(Optional<Security> security, ConcurrentApplicationContext context)
//    {
//	Optional<Security> filteredSecurity = filter(security, context);
//	
//	if(filteredSecurity.isEmpty())
//        {
//            security.ifPresent(s->context.fetch(securityOutpointsKey).add(getPortfolioRebalanceSecurityOutpoint(security, context)));
//        }
//	
//	return Optional.empty();
//    }
//    
//    private PortfolioRebalanceSecurityOutpoint getPortfolioRebalanceSecurityOutpoint(Optional<Security> security, ConcurrentApplicationContext context)
//    {
//	PortfolioRebalanceCommand portfolioRebalanceCommand = context.fetch(portfolioRebalanceCommandKey);
//	PortfolioDefinitionConfiguration portfolioDefinitionConfiguration = context.fetch(portfolioDefinitionConfigurationKey);
//	return PortfolioRebalanceSecurityOutpoint.builder()
//			.runId(portfolioRebalanceCommand.getRunId())
//			.portfolioDefinitionId(portfolioRebalanceCommand.getPortfolioDefinitionId())
//			.portfolioDefinitionConfigurationId(portfolioDefinitionConfiguration.getId())
//			.
//			.build();
//    }
//
//    protected abstract String getFilterOutpoint();
//
//    protected abstract Optional<Security> filter(Optional<Security> security, ConcurrentApplicationContext context);
//
//}
