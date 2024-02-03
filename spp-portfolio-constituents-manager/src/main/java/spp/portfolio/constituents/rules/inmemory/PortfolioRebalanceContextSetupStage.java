package spp.portfolio.constituents.rules.inmemory;

import static io.github.funofprograming.context.impl.ApplicationContextHolder.getGlobalContext;
import static io.github.funofprograming.context.impl.ApplicationContextHolder.setGlobalContext;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.findPortfolioDefinitionConfiguration;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.portfolioConfigurationKey;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.portfolioDefinitionConfigurationKey;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.portfolioRebalanceCommandKey;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.portfolioRebalanceKey;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.rebalanceContextNameBuilder;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.securityOutpointMapKey;
import static spp.portfolio.manager.utilities.json.JsonUtil.viaJson;

import java.util.concurrent.ConcurrentHashMap;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import io.github.funofprograming.context.impl.ConcurrentApplicationContextImpl;
import spp.portfolio.constituents.rebalance.PortfolioRebalanceCommand;
import spp.portfolio.constituents.rebalance.PortfolioRebalanceStage;
import spp.portfolio.model.definition.PortfolioDefinitionConfiguration;
import spp.portfolio.model.rebalance.PortfolioRebalance;

public class PortfolioRebalanceContextSetupStage implements PortfolioRebalanceStage
{
    @Override
    public PortfolioRebalance execute(PortfolioRebalanceCommand portfolioRebalanceCommand)
    {
        setGlobalContext(new ConcurrentApplicationContextImpl(rebalanceContextNameBuilder.apply(portfolioRebalanceCommand)));
        ConcurrentApplicationContext context = (ConcurrentApplicationContext) getGlobalContext(rebalanceContextNameBuilder.apply(portfolioRebalanceCommand));
        context.add(portfolioRebalanceCommandKey, portfolioRebalanceCommand);
        setPortfolioDefinition(context, portfolioRebalanceCommand);
        setConfiguration(context);
        setPortfolioRebalance(context);
        setSecurityOutpointMap(context);
        return context.fetch(portfolioRebalanceKey);
    }

    private void setSecurityOutpointMap(ConcurrentApplicationContext context)
    {
        context.add(securityOutpointMapKey, new ConcurrentHashMap<Long, String>());        
    }

    private void setPortfolioRebalance(ConcurrentApplicationContext context)
    {
        PortfolioRebalanceCommand portfolioRebalanceCommand = context.fetch(portfolioRebalanceCommandKey);
        PortfolioDefinitionConfiguration portfolioDefinitionConfiguration = context.fetch(portfolioDefinitionConfigurationKey);
        PortfolioRebalance portfolioRebalance = 
                PortfolioRebalance.builder()
                .runId(portfolioRebalanceCommand.getRunId())
                .date(portfolioRebalanceCommand.getDate())
                .portfolioDefinition(portfolioDefinitionConfiguration.getPortfolioDefinition())
                .portfolioDefinitionConfiguration(portfolioDefinitionConfiguration)
                .rebalanceType(portfolioRebalanceCommand.getPortfolioRebalanceType())
                .isActive(Boolean.TRUE)
                .build();
        context.add(portfolioRebalanceKey, portfolioRebalance);
    }
    
    private void setConfiguration(ConcurrentApplicationContext context)
    {
        PortfolioDefinitionConfiguration portfolioDefinitionConfiguration = context.fetch(portfolioDefinitionConfigurationKey);
        PortfolioConfiguration configuration = viaJson(portfolioDefinitionConfiguration.getConfiguration(), PortfolioConfiguration.class);
        context.add(portfolioConfigurationKey, configuration);
    }

    private void setPortfolioDefinition(ConcurrentApplicationContext context, PortfolioRebalanceCommand portfolioRebalanceCommand)
    {
        PortfolioDefinitionConfiguration portfolioDefinitionConfiguration = findPortfolioDefinitionConfiguration.apply(portfolioRebalanceCommand);        
        context.add(portfolioDefinitionConfigurationKey, portfolioDefinitionConfiguration);
    }

}
