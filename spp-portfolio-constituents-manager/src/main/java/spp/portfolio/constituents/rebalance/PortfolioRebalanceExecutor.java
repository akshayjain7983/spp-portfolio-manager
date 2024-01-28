package spp.portfolio.constituents.rebalance;

import java.util.concurrent.CompletableFuture;

import spp.portfolio.model.rebalance.PortfolioRebalance;

public interface PortfolioRebalanceExecutor
{
    CompletableFuture<PortfolioRebalance> execute(PortfolioRebalanceCommand portfolioRebalanceCommand);
}
