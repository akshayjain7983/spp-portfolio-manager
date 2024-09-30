package spp.portfolio.configuration.dao;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.transaction.Transactional;
import spp.portfolio.model.definition.PortfolioDefinition;

@Transactional
public interface PortfolioDefinitionRepository extends JpaRepository<PortfolioDefinition, Long>
{
    public Optional<PortfolioDefinition> findByNameAndEffectiveDateIsBeforeAndDiscontinuedDateIsAfter(String name, LocalDate effectiveDate, LocalDate discontinuedDate);
    
    public Collection<PortfolioDefinition> nameIsLikeAndEffectiveDateIsBeforeAndDiscontinuedDateIsAfter(String nameLike, LocalDate effectiveDate, LocalDate discontinuedDate);
    
    public Collection<PortfolioDefinition> effectiveDateIsBeforeAndDiscontinuedDateIsAfter(LocalDate effectiveDate, LocalDate discontinuedDate);
    
    public default Optional<PortfolioDefinition> findByName(String name)
    {
        return findByNameAndEffectiveDateIsBeforeAndDiscontinuedDateIsAfter(name, LocalDate.now(ZoneOffset.UTC), LocalDate.now(ZoneOffset.UTC));
    }
    
    public default Collection<PortfolioDefinition> nameIsLike(String nameLike)
    {
        return nameIsLikeAndEffectiveDateIsBeforeAndDiscontinuedDateIsAfter(nameLike, LocalDate.now(ZoneOffset.UTC), LocalDate.now(ZoneOffset.UTC));
    }
    
    public default Collection<PortfolioDefinition> findEffectiveOn(LocalDate date)
    {
        return effectiveDateIsBeforeAndDiscontinuedDateIsAfter(date, date);
    }
    
    public default Collection<PortfolioDefinition> findEffective()
    {
        return findEffectiveOn(LocalDate.now(ZoneOffset.UTC));
    }
}
