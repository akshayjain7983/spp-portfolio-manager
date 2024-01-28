package spp.portfolio.constituents.rules.inmemory;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import spp.portfolio.constituents.rebalance.PortfolioRebalanceCommand;
import spp.portfolio.constituents.rebalance.PortfolioRebalanceExecutor;
import spp.portfolio.constituents.rebalance.PortfolioRebalanceRule;
import spp.portfolio.model.rebalance.PortfolioRebalance;

@Component
public class PortfolioRebalanceInMemoryExecutor implements PortfolioRebalanceExecutor
{
    @Autowired
    private SortedSet<PortfolioRebalanceRule> portfolioRebalanceRules;
    
    @Override
    public PortfolioRebalance execute(PortfolioRebalanceCommand portfolioRebalanceCommand)
    {
        SortedSet<PortfolioRebalanceRule> portfolioRebalanceRulesExecuteAlways = portfolioRebalanceRules.stream().filter(PortfolioRebalanceRule::isExecuteAlways).collect(Collectors.toCollection(TreeSet::new));
        
        PortfolioRebalance portfolioRebalance = null;
        try 
        {
            for(PortfolioRebalanceRule rule: portfolioRebalanceRules)
            {
                portfolioRebalance = rule.execute(portfolioRebalanceCommand);
            }
        }
        catch(Throwable throwable)
        {
            for(PortfolioRebalanceRule rule: portfolioRebalanceRulesExecuteAlways)
            {
                rule.execute(portfolioRebalanceCommand);
            }
            
            throw throwable;
        }
        
        
        return portfolioRebalance;
    }
}
