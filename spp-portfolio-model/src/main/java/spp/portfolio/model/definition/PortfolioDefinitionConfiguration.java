package spp.portfolio.model.definition;

import java.time.Instant;
import java.time.LocalDate;

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
import lombok.Data;
import lombok.EqualsAndHashCode;
import spp.portfolio.model.definition.configuration.PortfolioConfiguration;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = {"id", "validFrom", "validTo"})
public class PortfolioDefinitionConfiguration
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "portfolio_definition_id", nullable = false)
    private PortfolioDefinition portfolioDefinition;
    
    private LocalDate validFrom;
    private LocalDate validTo;
    private PortfolioConfiguration configuration;
    @LastModifiedDate
    private Instant lastUpdatedTimestamp;
    private String lastUpdatedBy;
    private Boolean isActive;
}
