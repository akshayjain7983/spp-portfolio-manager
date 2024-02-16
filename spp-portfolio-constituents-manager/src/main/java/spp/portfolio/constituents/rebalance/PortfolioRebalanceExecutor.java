package spp.portfolio.constituents.rebalance;

import spp.portfolio.model.rebalance.PortfolioRebalance;

public interface PortfolioRebalanceExecutor
{
    PortfolioRebalance execute(PortfolioRebalanceCommand portfolioRebalanceCommand);
}
