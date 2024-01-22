package spp.portfolio.model.rebalance;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;

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
import lombok.Data;
import lombok.EqualsAndHashCode;
import spp.portfolio.model.definition.PortfolioDefinition;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = {"id"})
public class PortfolioRebalance
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private PortfolioDefinition portfolioDefinition;
    private String rebalanceType;
    private LocalDate date;
    @LastModifiedDate
    private Instant lastUpdatedTimestamp;
    private String lastUpdatedBy;
    private BigDecimal investmentMarketValue;
    private BigDecimal portfolioCash;
    @OneToMany(mappedBy = "portfolioRebalance", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Collection<PortfolioConstituent> portfolioConstituents;
}
