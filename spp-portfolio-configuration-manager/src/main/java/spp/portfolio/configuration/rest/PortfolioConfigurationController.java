package spp.portfolio.configuration.rest;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import spp.portfolio.configuration.dao.PortfolioDefinitionRepository;
import spp.portfolio.model.definition.PortfolioDefinition;
import spp.portfolio.model.exception.SppException;

@RestController
public class PortfolioConfigurationController
{
    @Autowired
    private PortfolioDefinitionRepository portfolioDefinitionRepository;
    
    @PostMapping("/portfolio-definition")
    public PortfolioDefinition createPortfolioDefinition(@RequestBody PortfolioDefinition portfolioDefinition)
    {
        Objects.requireNonNull(portfolioDefinition);
        Objects.requireNonNull(portfolioDefinition.getPortfolioDefinitionConfigurations());
        portfolioDefinition.getPortfolioDefinitionConfigurations().stream().forEach(pc->pc.setPortfolioDefinition(portfolioDefinition));
        return portfolioDefinitionRepository.save(portfolioDefinition);
    }
    
    @PutMapping("/portfolio-definition/{id}")
    public PortfolioDefinition updatePortfolioDefinition(@PathVariable Long id, @RequestBody PortfolioDefinition portfolioDefinition)
    {
        Objects.requireNonNull(id);
        Objects.requireNonNull(portfolioDefinition);
        Objects.requireNonNull(portfolioDefinition.getPortfolioDefinitionConfigurations());
        if(!portfolioDefinitionRepository.existsById(id)) 
            throw new SppException("Invalid id. Please use POST Http method for creation");
        
        portfolioDefinition.getPortfolioDefinitionConfigurations().stream().forEach(pc->pc.setPortfolioDefinition(portfolioDefinition));
        portfolioDefinition.setId(id);
        
        return portfolioDefinitionRepository.save(portfolioDefinition);
    }
    
    @GetMapping("/portfolio-definition/{id}")
    public Optional<PortfolioDefinition> getPortfolioDefinition(@PathVariable Long id)
    {
        Objects.requireNonNull(id);
        return portfolioDefinitionRepository.findById(id);
    }
}
