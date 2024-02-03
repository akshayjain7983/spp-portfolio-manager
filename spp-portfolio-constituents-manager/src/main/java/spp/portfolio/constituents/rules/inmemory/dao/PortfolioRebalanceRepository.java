package spp.portfolio.constituents.rules.inmemory.dao;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.transaction.Transactional;
import spp.portfolio.model.definition.PortfolioDefinition;
import spp.portfolio.model.rebalance.PortfolioRebalance;
import spp.portfolio.model.rebalance.PortfolioRebalanceType;

@Transactional
public interface PortfolioRebalanceRepository extends JpaRepository<PortfolioRebalance, Long>
{
    Optional<PortfolioRebalance> findByPortfolioDefinitionAndDateAndRebalanceTypeAndIsActive(PortfolioDefinition portfolioDefinition, LocalDate date, PortfolioRebalanceType rebalanceType, Boolean isActive);
    
    Optional<PortfolioRebalance> findByRunIdAndIsActive(UUID runId, Boolean isActive);
}
