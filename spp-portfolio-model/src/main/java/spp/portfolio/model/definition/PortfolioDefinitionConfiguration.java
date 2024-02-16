package spp.portfolio.model.definition;

import java.time.Instant;
import java.time.LocalDate;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import spp.portfolio.model.definition.configuration.PortfolioConfiguration;

@Data
@Entity
@Table(catalog = "spp", schema = "spp", name = "portfolio_definition_configuration")
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = {"id", "validFrom", "validTo"})
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioDefinitionConfiguration
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "portfolio_definition_id", nullable = false)
    private PortfolioDefinition portfolioDefinition;
    
    private LocalDate validFrom;
    private LocalDate validTo;
    @JdbcTypeCode(SqlTypes.JSON)
    private PortfolioConfiguration configuration;
    @LastModifiedDate
    private Instant lastUpdatedTimestamp;
    private String lastUpdatedBy;
    private Boolean isActive;
}
