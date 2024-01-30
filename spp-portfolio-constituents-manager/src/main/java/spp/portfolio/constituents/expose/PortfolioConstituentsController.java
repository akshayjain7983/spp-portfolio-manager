package spp.portfolio.constituents.expose;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import spp.portfolio.constituents.rebalance.PortfolioRebalanceCommand;
import spp.portfolio.constituents.rebalance.PortfolioRebalanceExecutor;
import spp.portfolio.constituents.rules.inmemory.dao.PortfolioRebalanceRepository;
import spp.portfolio.model.definition.PortfolioDefinition;
import spp.portfolio.model.definition.PortfolioDefinitionConfiguration;
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
    @PostMapping("/portfolio-constituents/{portfolioDefinitionId}/{portfolioRebalanceType}")
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
        Optional<PortfolioRebalance> rebalance = portfolioRebalanceRepository.findByRunId(runId);
        return sanitizeRebalance(rebalance);
    }
    
    @Override
    @GetMapping("/portfolio-constituents/{portfolioDefinitionId}/{portfolioRebalanceType}")
    public Optional<PortfolioRebalance> getRebalance(
            @PathVariable Long portfolioDefinitionId
            , @PathVariable PortfolioRebalanceType portfolioRebalanceType
            , @RequestParam LocalDate rebalanceDate)
    {
        Optional<PortfolioRebalance> rebalance = portfolioRebalanceRepository.findByPortfolioDefinitionAndDateAndRebalanceTypeAndIsActive(PortfolioDefinition.builder().id(portfolioDefinitionId).build(), rebalanceDate, portfolioRebalanceType, Boolean.TRUE); 
        return sanitizeRebalance(rebalance);
    }
    
    private Optional<PortfolioRebalance> sanitizeRebalance(Optional<PortfolioRebalance> rebalance)
    {
        return rebalance.map(r->{
            
            //need only portfolio definition id
            r.setPortfolioDefinition(PortfolioDefinition.builder().id(r.getPortfolioDefinition().getId()).build());
            
          //need only portfolio definition configuration id
            r.setPortfolioDefinitionConfiguration(PortfolioDefinitionConfiguration.builder().id(r.getPortfolioDefinitionConfiguration().getId()).build());
            
            return r;
        });
    }
    
}
