package spp.portfolio.configuration.dao;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import spp.portfolio.model.definition.PortfolioDefinition;

@RepositoryRestResource(path = "portfolio-definition")
public interface PortfolioDefinitionRepository extends JpaRepository<PortfolioDefinition, Long>
{
    public Optional<PortfolioDefinition> findByName(String name);
    
    public Collection<PortfolioDefinition> nameIsLike(String nameLike);
    
    public default PortfolioDefinition upsert(PortfolioDefinition portfolioDefinition)
    {
        Objects.requireNonNull(portfolioDefinition);
        Objects.requireNonNull(portfolioDefinition.getPortfolioDefinitionConfigurations());
        
        portfolioDefinition.getPortfolioDefinitionConfigurations().stream().forEach(pc->{
            pc.setPortfolioDefinition(portfolioDefinition);
        });
        
        return save(portfolioDefinition);
    }
}
