package spp.portfolio.configuration.dao;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import spp.portfolio.model.definition.PortfolioDefinition;

public interface PortfolioDefinitionRepository extends JpaRepository<PortfolioDefinition, Long>
{
    public Optional<PortfolioDefinition> findByNameAndEffectiveDateIsBeforeAndDiscontinuedDateIsAfterAndPortfolioDefinitionConfigurationsIsActive(String name, LocalDate effectiveDate, LocalDate discontinuedDate, Boolean isActive);
    
    public Collection<PortfolioDefinition> nameIsLikeAndEffectiveDateIsBeforeAndDiscontinuedDateIsAfterAndPortfolioDefinitionConfigurationsIsActive(String nameLike, LocalDate effectiveDate, LocalDate discontinuedDate, Boolean isActive);
    
    public Optional<PortfolioDefinition> findByIdAndPortfolioDefinitionConfigurationsIsActive(Long id, Boolean isActive);
    
    public Collection<PortfolioDefinition> effectiveDateIsBeforeAndDiscontinuedDateIsAfterAndPortfolioDefinitionConfigurationsIsActive(LocalDate effectiveDate, LocalDate discontinuedDate, Boolean isActive);
    
    @Override
    public default Optional<PortfolioDefinition> findById(Long id)
    {
        return findByIdAndPortfolioDefinitionConfigurationsIsActive(id, true);
    }
    
    public default Optional<PortfolioDefinition> findByName(String name)
    {
        return findByNameAndEffectiveDateIsBeforeAndDiscontinuedDateIsAfterAndPortfolioDefinitionConfigurationsIsActive(name, LocalDate.now(ZoneOffset.UTC), LocalDate.now(ZoneOffset.UTC), true);
    }
    
    public default Collection<PortfolioDefinition> nameIsLike(String nameLike)
    {
        return nameIsLikeAndEffectiveDateIsBeforeAndDiscontinuedDateIsAfterAndPortfolioDefinitionConfigurationsIsActive(nameLike, LocalDate.now(ZoneOffset.UTC), LocalDate.now(ZoneOffset.UTC), true);
    }
    
    public default Collection<PortfolioDefinition> findEffectiveOn(LocalDate date)
    {
        return effectiveDateIsBeforeAndDiscontinuedDateIsAfterAndPortfolioDefinitionConfigurationsIsActive(date, date, true);
    }
    
    public default Collection<PortfolioDefinition> findEffective()
    {
        return findEffectiveOn(LocalDate.now(ZoneOffset.UTC));
    }
}
