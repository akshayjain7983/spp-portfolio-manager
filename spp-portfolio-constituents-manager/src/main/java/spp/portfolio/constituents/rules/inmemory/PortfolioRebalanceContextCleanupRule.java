package spp.portfolio.constituents.rules.inmemory;

import static io.github.funofprograming.context.impl.ApplicationContextHolder.clearGlobalContext;
import static io.github.funofprograming.context.impl.ApplicationContextHolder.getGlobalContext;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.portfolioRebalanceKey;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.rebalanceContextNameBuilder;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import spp.portfolio.constituents.rebalance.PortfolioRebalanceCommand;
import spp.portfolio.constituents.rebalance.PortfolioRebalanceRule;
import spp.portfolio.model.rebalance.PortfolioRebalance;

public class PortfolioRebalanceContextCleanupRule implements PortfolioRebalanceRule
{
    @Override
    public int getOrder()
    {
        return 3;
    }

    @Override
    public PortfolioRebalance execute(PortfolioRebalanceCommand portfolioRebalanceCommand)
    {
        ConcurrentApplicationContext context = (ConcurrentApplicationContext) getGlobalContext(rebalanceContextNameBuilder.apply(portfolioRebalanceCommand));
        PortfolioRebalance portfolioRebalance = context.fetch(portfolioRebalanceKey);
        clearGlobalContext(rebalanceContextNameBuilder.apply(portfolioRebalanceCommand));
        return portfolioRebalance;
    }
    
    public boolean isExecuteAlways()
    {
        return true;
    }
}
