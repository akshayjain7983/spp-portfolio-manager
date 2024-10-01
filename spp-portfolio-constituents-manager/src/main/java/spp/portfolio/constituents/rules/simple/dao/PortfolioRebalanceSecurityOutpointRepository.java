package spp.portfolio.constituents.rules.simple.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import spp.portfolio.model.rebalance.PortfolioRebalanceSecurityOutpoint;

public interface PortfolioRebalanceSecurityOutpointRepository extends JpaRepository<PortfolioRebalanceSecurityOutpoint, Long>
{

}
