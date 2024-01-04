package spp.portfolio.configuration.rest;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import spp.portfolio.configuration.dao.PortfolioDefinitionRepository;
import spp.portfolio.model.definition.PortfolioDefinition;

@RestController
public class PortfolioConfigurationController
{
    @Autowired
    private PortfolioDefinitionRepository portfolioDefinitionRepository;
    
    @PostMapping("/portfolio-definition")
    public PortfolioDefinition createPortfolioDefinition(@RequestBody PortfolioDefinition portfolioDefinition)
    {
        Objects.requireNonNull(portfolioDefinition);
        return portfolioDefinitionRepository.upsert(portfolioDefinition);
    }
    
    @PutMapping("/portfolio-definition/{id}")
    public PortfolioDefinition updatePortfolioDefinition(@RequestParam Long id, @RequestBody PortfolioDefinition portfolioDefinition)
    {
        Objects.requireNonNull(id);
        Objects.requireNonNull(portfolioDefinition);
        portfolioDefinition.setId(id);
        return portfolioDefinitionRepository.upsert(portfolioDefinition);
    }
}
