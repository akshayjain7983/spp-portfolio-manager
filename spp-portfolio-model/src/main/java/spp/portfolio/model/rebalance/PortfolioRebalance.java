package spp.portfolio.model.rebalance;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import spp.portfolio.model.definition.PortfolioDefinition;
import spp.portfolio.model.definition.PortfolioDefinitionConfiguration;

@Data
@Entity
@Table(catalog = "spp", schema = "spp", name = "portfolio_rebalance")
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = {"id"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioRebalance
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID runId;
    @ManyToOne(fetch = FetchType.LAZY)
    private PortfolioDefinition portfolioDefinition;
    @ManyToOne(fetch = FetchType.LAZY)
    private PortfolioDefinitionConfiguration portfolioDefinitionConfiguration;
    private PortfolioRebalanceType rebalanceType;
    private LocalDate date;
    @CreatedDate
    private Instant createdTimestamp;
    private String createdBy;
    @LastModifiedDate
    private Instant lastUpdatedTimestamp;
    private String lastUpdatedBy;
    private Boolean isActive;
    private BigDecimal investmentMarketValue;
    private BigDecimal portfolioCash;
    @OneToMany(mappedBy = "portfolioRebalance", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Collection<PortfolioConstituent> portfolioConstituents;
    @OneToMany(mappedBy = "portfolioRebalance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Collection<PortfolioRebalanceTransaction> portfolioRebalanceTransactions;
}
