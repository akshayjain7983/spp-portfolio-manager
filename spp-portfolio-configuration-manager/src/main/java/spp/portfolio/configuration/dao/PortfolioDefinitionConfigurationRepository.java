package spp.portfolio.configuration.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import spp.portfolio.model.definition.PortfolioDefinitionConfiguration;

public interface PortfolioDefinitionConfigurationRepository extends JpaRepository<PortfolioDefinitionConfiguration, Long>
{

}
