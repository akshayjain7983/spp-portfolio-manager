package spp.portfolio.configuration.expose;

import java.util.Optional;

import spp.portfolio.model.definition.PortfolioDefinition;

public interface PortfolioConfigurationManager
{

    PortfolioDefinition createPortfolioDefinition(PortfolioDefinition portfolioDefinition);

    PortfolioDefinition updatePortfolioDefinition(Long id, PortfolioDefinition portfolioDefinition);

    Optional<PortfolioDefinition> getPortfolioDefinition(Long id);

}