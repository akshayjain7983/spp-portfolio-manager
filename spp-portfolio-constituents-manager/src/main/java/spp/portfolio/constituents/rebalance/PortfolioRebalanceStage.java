package spp.portfolio.constituents.rebalance;

import spp.portfolio.model.rebalance.PortfolioRebalance;

public interface PortfolioRebalanceStage
{
    PortfolioRebalance execute(PortfolioRebalanceCommand portfolioRebalanceCommand);
    
    default boolean isExecuteAlways()
    {
        return false;
    }
}
