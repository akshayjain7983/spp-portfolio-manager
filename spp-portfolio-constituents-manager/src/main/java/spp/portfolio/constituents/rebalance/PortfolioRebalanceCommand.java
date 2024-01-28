package spp.portfolio.constituents.rebalance;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;
import spp.portfolio.model.rebalance.PortfolioRebalanceType;

@Data
@Builder
public class PortfolioRebalanceCommand
{
    private UUID runId;
    private Long portfolioDefinitionId;
    private LocalDate date;
    private PortfolioRebalanceType portfolioRebalanceType;
}
