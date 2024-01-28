package spp.portfolio.constituents.expose;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import spp.portfolio.constituents.rebalance.PortfolioRebalanceCommand;
import spp.portfolio.constituents.rebalance.PortfolioRebalanceExecutor;
import spp.portfolio.constituents.rules.inmemory.dao.PortfolioRebalanceRepository;
import spp.portfolio.model.definition.PortfolioDefinition;
import spp.portfolio.model.rebalance.PortfolioRebalance;
import spp.portfolio.model.rebalance.PortfolioRebalanceType;

@RestController
public class PortfolioConstituentsController implements PortfolioConstituentsManager
{
    @Autowired
    private PortfolioRebalanceExecutor portfolioRebalanceExecutor;
    
    @Autowired
    private PortfolioRebalanceRepository portfolioRebalanceRepository;
    
    @Override
    @PutMapping("/portfolio-constituents/trigger-rebalance/{portfolioDefinitionId}/{portfolioRebalanceType}")
    public UUID triggerRebalance(
            @PathVariable Long portfolioDefinitionId
            , @PathVariable PortfolioRebalanceType portfolioRebalanceType
            , @RequestParam LocalDate rebalanceDate
            )
    {
        UUID runId = UUID.randomUUID();
        PortfolioRebalanceCommand portfolioRebalanceCommand = 
                PortfolioRebalanceCommand.builder()
                .runId(runId)
                .portfolioDefinitionId(portfolioDefinitionId)
                .portfolioRebalanceType(portfolioRebalanceType)
                .date(rebalanceDate)
                .build();
        portfolioRebalanceExecutor.execute(portfolioRebalanceCommand);
        return runId;
    }

    @Override
    @GetMapping("/portfolio-constituents/{runId}")
    public Optional<PortfolioRebalance> getRebalance(@PathVariable UUID runId)
    {
        return portfolioRebalanceRepository.findByRunId(runId);
    }
    
    @Override
    @GetMapping("/portfolio-constituents/{portfolioDefinitionId}/{portfolioRebalanceType}")
    public Optional<PortfolioRebalance> getRebalance(
            @PathVariable Long portfolioDefinitionId
            , @PathVariable PortfolioRebalanceType portfolioRebalanceType
            , @RequestParam LocalDate rebalanceDate)
    {
        return portfolioRebalanceRepository.findByPortfolioDefinitionAndDateAndRebalanceTypeAndIsActive(PortfolioDefinition.builder().id(portfolioDefinitionId).build(), rebalanceDate, portfolioRebalanceType, Boolean.TRUE);
    }
    
}
