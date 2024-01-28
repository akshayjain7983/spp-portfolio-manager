package spp.portfolio.constituents.rebalance;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;
import spp.portfolio.model.rebalance.PortfolioRebalanceType;

@Data
@Builder
public class PortfolioRebalanceCommand
{
    private Long portfolioDefinitionId;
    private LocalDate date;
    private PortfolioRebalanceType portfolioRebalanceType;
}
