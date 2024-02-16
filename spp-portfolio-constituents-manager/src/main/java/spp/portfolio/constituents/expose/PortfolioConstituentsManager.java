package spp.portfolio.constituents.expose;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.web.bind.annotation.PathVariable;

import spp.portfolio.model.rebalance.PortfolioRebalance;
import spp.portfolio.model.rebalance.PortfolioRebalanceType;

public interface PortfolioConstituentsManager
{
    Map<LocalDate, String> rebalance(Long portfolioDefinitionId, PortfolioRebalanceType portfolioRebalanceType, LocalDate fromDate, LocalDate toDate);
    
    Optional<PortfolioRebalance> getRebalance(@PathVariable UUID runId);
    
    Optional<PortfolioRebalance> getRebalance(Long portfolioDefinitionId, PortfolioRebalanceType portfolioRebalanceType, LocalDate rebalanceDate);
}
