package spp.portfolio.model.rebalance;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
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

@Data
@Entity
@Table(catalog = "spp", schema = "spp", name = "portfolio_rebalance_transactions")
@EqualsAndHashCode(of = {"id"})
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioRebalanceTransaction
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "portfolio_rebalance_id", nullable = false)
    private PortfolioRebalance portfolioRebalance;
    private Long securityId;
    private PortfolioRebalanceTransactionType transactionType;
    private BigDecimal price;
    private Long units;
}
