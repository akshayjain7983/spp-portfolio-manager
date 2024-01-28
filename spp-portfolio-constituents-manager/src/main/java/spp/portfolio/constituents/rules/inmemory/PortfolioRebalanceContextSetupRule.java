package spp.portfolio.constituents.rules.inmemory;

import static io.github.funofprograming.context.impl.ApplicationContextHolder.getGlobalContext;
import static io.github.funofprograming.context.impl.ApplicationContextHolder.setGlobalContext;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.portfolioConfigurationKey;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.portfolioDefinitionConfigurationKey;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.portfolioRebalanceCommandKey;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.portfolioRebalanceKey;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.rebalanceContextNameBuilder;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.securityDataDaoKey;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.securityOutpointMapKey;
import static spp.portfolio.manager.utilities.json.JsonUtil.viaJson;

import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import io.github.funofprograming.context.impl.ConcurrentApplicationContextImpl;
import spp.portfolio.configuration.expose.PortfolioConfigurationManager;
import spp.portfolio.constituents.rebalance.PortfolioRebalanceCommand;
import spp.portfolio.constituents.rebalance.PortfolioRebalanceRule;
import spp.portfolio.constituents.rules.inmemory.dao.SecurityDataDao;
import spp.portfolio.model.definition.PortfolioDefinition;
import spp.portfolio.model.definition.PortfolioDefinitionConfiguration;
import spp.portfolio.model.exception.SppException;
import spp.portfolio.model.rebalance.PortfolioRebalance;

@Component
public class PortfolioRebalanceContextSetupRule implements PortfolioRebalanceRule
{
    @Autowired
    private PortfolioConfigurationManager portfolioConfigurationManager;
    
    @Autowired
    private SecurityDataDao securityDataDao;
    
    @Override
    public PortfolioRebalance execute(PortfolioRebalanceCommand portfolioRebalanceCommand)
    {
        setGlobalContext(new ConcurrentApplicationContextImpl(rebalanceContextNameBuilder.apply(portfolioRebalanceCommand)));
        ConcurrentApplicationContext context = (ConcurrentApplicationContext) getGlobalContext(rebalanceContextNameBuilder.apply(portfolioRebalanceCommand));
        context.add(portfolioRebalanceCommandKey, portfolioRebalanceCommand);
        setPortfolioDefinition(context, portfolioRebalanceCommand);
        setDaos(context);
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
                .date(portfolioRebalanceCommand.getDate())
                .portfolioDefinition(portfolioDefinitionConfiguration.getPortfolioDefinition())
                .portfolioDefinitionConfiguration(portfolioDefinitionConfiguration)
                .rebalanceType(portfolioRebalanceCommand.getPortfolioRebalanceType())
                .isActive(Boolean.TRUE)
                .build();
        context.add(portfolioRebalanceKey, portfolioRebalance);
    }

    @Override
    public int getOrder()
    {
        return 0;
    }
    
    private void setConfiguration(ConcurrentApplicationContext context)
    {
        PortfolioDefinitionConfiguration portfolioDefinitionConfiguration = context.fetch(portfolioDefinitionConfigurationKey);
        PortfolioConfiguration configuration = viaJson(portfolioDefinitionConfiguration.getConfiguration(), PortfolioConfiguration.class);
        context.add(portfolioConfigurationKey, configuration);
    }

    private void setDaos(ConcurrentApplicationContext context)
    {
        context.add(securityDataDaoKey, securityDataDao);
    }

    private void setPortfolioDefinition(ConcurrentApplicationContext context, PortfolioRebalanceCommand portfolioRebalanceCommand)
    {
        PortfolioDefinition portfolioDefinition = portfolioConfigurationManager.getPortfolioDefinition(portfolioRebalanceCommand.getPortfolioDefinitionId()).orElseThrow(()->new SppException("Invalid portfolio definition id. No portfolio definition found"));
        LocalDate rebalanceDate = portfolioRebalanceCommand.getDate();
        if(rebalanceDate.isBefore(portfolioDefinition.getEffectiveDate()) || !rebalanceDate.isBefore(portfolioDefinition.getDiscontinuedDate()))
            throw new SppException("Portfolio definition is not effective on rebalance date");
        
        PortfolioDefinitionConfiguration portfolioDefinitionConfiguration =
                portfolioDefinition.getPortfolioDefinitionConfigurations()
                .stream()
                .filter(pdc->rebalanceDate.isAfter(pdc.getValidFrom()) || (pdc.getValidFrom().isEqual(portfolioDefinition.getEffectiveDate()) && pdc.getValidFrom().isEqual(rebalanceDate)))
                .filter(pdc->!rebalanceDate.isAfter(pdc.getValidTo()))
                .findFirst()
                .orElseThrow(()->new SppException("Portfolio definition does not have valid configuration for rebalance date"));
        
        context.add(portfolioDefinitionConfigurationKey, portfolioDefinitionConfiguration);
    }

}
