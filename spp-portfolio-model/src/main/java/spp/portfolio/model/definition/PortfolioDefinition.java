package spp.portfolio.model.definition;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(catalog = "spp", schema = "spp", name = "portfolio_definition")
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = {"id", "name", "effectiveDate", "discontinuedDate"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioDefinition
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private LocalDate effectiveDate;
    private LocalDate discontinuedDate;
    @LastModifiedDate
    private Instant lastUpdatedTimestamp;
    private String lastUpdatedBy;
    @OneToMany(mappedBy = "portfolioDefinition", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<PortfolioDefinitionConfiguration> portfolioDefinitionConfigurations;
}
