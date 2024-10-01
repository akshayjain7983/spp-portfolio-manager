package spp.portfolio.model.rebalance;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "portfolio_rebalance_security_outpoint")
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = {"id", "runId", "securityId"})
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioRebalanceSecurityOutpoint
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID runId;
    private Long portfolioDefinitionId;
    private Long portfolioDefinitionConfigurationId;
    private PortfolioRebalanceType rebalanceType;
    private LocalDate rebalanceDate;
    private Long securityId;
    private String outpoint;
    @CreatedDate
    private Instant outpointTimestamp;
}
