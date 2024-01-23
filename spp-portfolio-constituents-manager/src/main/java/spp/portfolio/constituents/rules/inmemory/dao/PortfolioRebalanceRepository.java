package spp.portfolio.constituents.rules.inmemory.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import spp.portfolio.model.rebalance.PortfolioRebalance;

public interface PortfolioRebalanceRepository extends JpaRepository<PortfolioRebalance, Long>
{

}
