package spp.portfolio.constituents.rebalance;

import java.util.Comparator;

import org.springframework.core.Ordered;

import spp.portfolio.model.rebalance.PortfolioRebalance;

public interface PortfolioRebalanceRule extends Ordered, Comparable<PortfolioRebalanceRule>
{
    PortfolioRebalance execute(PortfolioRebalanceCommand portfolioRebalanceCommand);
    
    default boolean isExecuteAlways()
    {
        return false;
    }
    
    default int compareTo(PortfolioRebalanceRule other)
    {
        return Comparator.comparing(PortfolioRebalanceRule::getOrder).compare(this, other);
    }
}
